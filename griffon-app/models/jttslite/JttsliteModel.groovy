package jttslite

import ca.odell.glazedlists.*
import ca.odell.glazedlists.swing.GlazedListsSwing

import griffon.transform.PropertyListener

@PropertyListener (enabler)
class JttsliteModel {
    @Bindable boolean working
    @Bindable String status
    @Bindable Long selectedTaskId
    @Bindable Integer selectedTaskIndex    //index of the selected task, it's used in order to workaround http://java.net/jira/browse/GLAZEDLISTS-462
    @Bindable Long workingLogId
    @Bindable Long workspaceId

    @Bindable boolean startEnabled
    @Bindable boolean stopEnabled

    def workingLog
    List workingPathIds //ids of tasks on the path from the "running" worklog to the root

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
     * the task list
     */
    BasicEventList taskList = GlazedLists.threadSafeList(new BasicEventList ())
    ObservableElementList observableTaskList = new ObservableElementList (taskList, GlazedLists.beanConnector (ObservableMap.class))
    DisposableMap taskMap = GlazedLists.syncEventListToMap(observableTaskList, new TaskKeyMaker ())
    EventList swingProxyTaskList = GlazedListsSwing.swingThreadProxyList(observableTaskList)//hack to avoid NPE on GL treetable
    def taskTreeNodeComparator = {a,b->
        def level = a.treeDepth <=> b.treeDepth
        if (level!=0) {
            return level
        }
        return a.siblingIndex <=> b.siblingIndex
    } as Comparator
    TreeList taskTreeList = new TreeList(new SortedList (swingProxyTaskList, taskTreeNodeComparator), new TaskTreeFormat(), TreeList.NODES_START_EXPANDED)

    EventList worklogList = GlazedLists.threadSafeList(new BasicEventList ())
    ObservableElementList observableWorklogList = new ObservableElementList (worklogList, GlazedLists.beanConnector (ObservableMap.class))
    DisposableMap worklogMap = GlazedLists.syncEventListToMap(worklogList, new WorklogKeyMaker ())
    EventList swingProxyWorklogList = GlazedListsSwing.swingThreadProxyList(observableWorklogList)





    public void setSelectedTaskId (Long taskId) {
        selectedTaskId = taskId
        worklogList.clear()
        if (taskId!=null) {
            execOutsideUI {
                def newData = worklogService.getWorklogs(taskId).collect {it as ObservableMap}
                execInsideUIAsync {
                    worklogList.addAll(newData)
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

    class TaskKeyMaker implements FunctionList.Function<Map, Object> {

        Object evaluate(Map sourceValue) {
            return sourceValue.id
        }
    }
    class WorklogKeyMaker implements FunctionList.Function<Map, Object> {

        Object evaluate(Map sourceValue) {
            return sourceValue.id
        }
    }
}
