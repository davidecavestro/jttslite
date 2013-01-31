package jttslite

import ca.odell.glazedlists.*
import ca.odell.glazedlists.swing.GlazedListsSwing

import griffon.transform.PropertyListener
import griffon.transform.Threading

@PropertyListener (enabler)
class JttsliteModel {
    @Bindable boolean inProgress
    @Bindable String status
    @Bindable Long selectedTaskId
    @Bindable Long inProgressWorklogId
    @Bindable Long workspaceId

    @Bindable boolean startEnabled
    @Bindable boolean stopEnabled

    TaskService taskService
    WorklogService worklogService

    /*
     a closure to enable/disable buttons
    */
    private enabler = {e->
        startEnabled = selectedTaskId!=null && !inProgress
        stopEnabled = inProgress
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

    public void setInProgressWorklogId (Long worklogId) {
        inProgressWorklogId = worklogId
        def running = inProgressWorklogId!=null
        inProgress = running
        if (running) {
            status = app.getMessage('application.title')
        } else {

        }
    }


    void mvcGroupInit(Map args) {
        status = "Welcome to ${GriffonNameUtils.capitalize(app.getMessage('application.title'))}"
    }

    private class TaskTreeFormat implements TreeList.Format {
        public void getPath(List path, Object element) {
            def taskPath = taskService.getTaskPath (element.id)

            def elems = taskPath.collect {elem->
                def origElem = taskMap.get(elem.id)
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
