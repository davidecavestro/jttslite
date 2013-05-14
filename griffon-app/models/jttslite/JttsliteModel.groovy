/*
 * Copyright (c) 2013, the original author or authors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package jttslite

import ca.odell.glazedlists.*
import ca.odell.glazedlists.swing.GlazedListsSwing

import griffon.transform.PropertyListener

@PropertyListener (enabler)
class JttsliteModel {
    @Bindable boolean working
    @Bindable String status
    @Bindable Long selectedTaskId
    @Bindable List<TaskBean> taskSelection = new ObservableList()
    @Bindable List<WorklogBean> worklogSelection = new ObservableList()
    @Bindable Long workingLogId
    @Bindable Long workspaceId
    @Bindable boolean tasksSelected
    @Bindable boolean worklogsSelected

    @Bindable boolean startEnabled
    @Bindable boolean stopEnabled

    WorklogBean workingLog
    List<Long> workingPathIds //ids of tasks on the path from the "running" worklog to the root

    TaskService taskService
    WorklogService worklogService

    /*
     a closure to enable/disable buttons
    */
    private enabler = {e->
        startEnabled = selectedTaskId!=null && !working
        stopEnabled = working
    }

    /**
     * the task list: represents the  tasks on the current workspace
     */
    BasicEventList<TaskBean> taskList = GlazedLists.threadSafeList(new BasicEventList ())
    ObservableElementList<TaskBean> observableTaskList = new ObservableElementList (taskList, GlazedLists.beanConnector (TaskBean.class))
    DisposableMap<Long, TaskBean> taskMap = GlazedLists.syncEventListToMap(observableTaskList, new TaskKeyMaker ())
    EventList<TaskBean> swingProxyTaskList = GlazedListsSwing.swingThreadProxyList(observableTaskList)//hack to avoid NPE on GL treetable
    def taskTreeNodeComparator = {a,b->
        def level = a.treeDepth <=> b.treeDepth
        if (level!=0) {
            return level
        }
        return a.siblingIndex <=> b.siblingIndex
    } as Comparator
    TreeList<TaskBean> taskTreeList = new TreeList(new SortedList (swingProxyTaskList, taskTreeNodeComparator), new TaskTreeFormat(), TreeList.NODES_START_EXPANDED)

    /**
     * The worklog list: represents the worklogs of the selected tasks
     */
    EventList<WorklogBean> worklogList = GlazedLists.threadSafeList(new BasicEventList ())
    ObservableElementList<WorklogBean> observableWorklogList = new ObservableElementList (worklogList, GlazedLists.beanConnector (WorklogBean.class))
    DisposableMap<Long, WorklogBean> worklogMap = GlazedLists.syncEventListToMap(worklogList, new WorklogKeyMaker ())
    EventList<WorklogBean> swingProxyWorklogList = GlazedListsSwing.swingThreadProxyList(observableWorklogList)





    public void setSelectedTaskId (Long taskId) {
        selectedTaskId = taskId
        worklogList.clear()//clear previous worklog list
        if (taskId!=null) {
            execOutsideUI {
                def newData = worklogService.getWorklogs(taskId)
                execInsideUIAsync {
                    log.debug("Showing worklogs for taskId $taskId: $newData")
                    worklogList.addAll(newData)//show  worklogs for current selection
                }
            }
        }
    }

    public void setWorkingLogId (Long worklogId) {
        workingLogId = worklogId
        working = workingLogId!=null
        if (working) {
            status = app.getMessage('status.working')
            workingLog = worklogService.getWorklog(worklogId)
            workingPathIds = taskService.getTaskPathIds(workingLog.taskId)
        } else {
            workingPathIds = null
            workingLog = null
        }
    }


    void mvcGroupInit(Map args) {
        status = "Welcome to ${GriffonNameUtils.capitalize(app.getMessage('application.title'))}"
    }


    public void setTaskSelection (List<TaskBean> taskSelection) {
        this.taskSelection.clear ()
        if (taskSelection) {
            this.taskSelection.addAll (taskSelection)
        }

        setTasksSelected (!taskSelection.isEmpty())
        println "tasksSelected: $tasksSelected"
    }

    public void setWorklogSelection (List<TaskBean> worklogSelection) {
        this.worklogSelection.clear ()
        if (worklogSelection) {
            this.worklogSelection.addAll (worklogSelection)
        }

        setWorklogsSelected (!worklogSelection.isEmpty())
        println "worklogsSelected: $worklogsSelected"
    }

    private class TaskTreeFormat implements TreeList.Format {
        public void getPath(List path, Object element) {
            def taskPath = taskService.getTaskPathIds (element.id)

            def elems = taskPath.collect {pathId->
                def origElem = taskMap.get(pathId)
                return origElem
            }
            path.addAll (elems)
        }

        public boolean allowsChildren(Object element) {
            return true;
        }

        @Override
        public Comparator getComparator(int depth) {
            {a, b -> a.siblingIndex <=> b.siblingIndex} as Comparator
        }
    }

    class TaskKeyMaker implements FunctionList.Function<TaskBean, Long> {

        Long evaluate(TaskBean sourceValue) {
            return sourceValue.id
        }
    }
    class WorklogKeyMaker implements FunctionList.Function<WorklogBean, Long> {

        Long evaluate(WorklogBean sourceValue) {
            return sourceValue.id
        }
    }
}
