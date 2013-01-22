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

    WorkspaceService workspaceService
    TaskService taskService
    WorklogService worklogService
    DictionaryService dictionaryService

    /**
     * the task list
     */
    BasicEventList taskList = GlazedLists.threadSafeList(new BasicEventList ())
    ObservableElementList observableTaskList = new ObservableElementList (taskList, GlazedLists.beanConnector (ObservableMap.class))
    DisposableMap taskMap = GlazedLists.syncEventListToMap(taskList, new TaskKeyMaker ())
    EventList swingProxyTaskList = GlazedListsSwing.swingThreadProxyList(observableTaskList)//hack to avoid NPE on GL treetable
    def taskTreeNodeComparator = {a,b->
        def level = a.treeLevel <=> b.treeLevel
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

    private final static String LAST_WORKSPACE = 'lastWorkspace'

    void mvcGroupInit(Map args) {
        status = "Welcome to ${GriffonNameUtils.capitalize(app.getMessage('application.title'))}"
    }

    public void setSelectedTaskId (Long taskId) {
        selectedTaskId = taskId
        worklogList.clear()
        if (taskId!=null) {
            worklogList.addAll(worklogService.getWorklogs(taskId).collect {it as ObservableMap})
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

    /*
     a closure to enable/disable buttons
    */
    private enabler = {e->
        startEnabled = selectedTaskId!=null && !inProgress
        stopEnabled = inProgress
    }

    def loadTasksForWorkspace(def workspaceId) {
        loadTasks (taskService.getTasks(workspaceId).collect {it as ObservableMap})
        loadRunningWorklog (workspaceId)
    }

    def loadRunningWorklog(def workspaceId) {
        setInProgressWorklogId (worklogService.getRunningWorklog (workspaceId)?.id)
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    def loadTasks(def tasks) {
//        edt{
            taskList.addAll (tasks)
//        }
    }

    /**
     * Load the last workspace loaded (upon restart). If this is the first run the last workspace loaded is no more
     * available loads the first available workspace.
     *
     * <p>
     *     Loading a workspace implies loading its data into application UI.
     * </p>
     */
    def loadLastWorkspace() {
        workspaceId = dictionaryService.getLongValue(LAST_WORKSPACE)
        if (workspaceId==null || !workspaceService.getWorkspace (workspaceId)) {
            /*
            if there's no last workspace memory or it is no more available
            load the first available workspace
             */
            workspaceId = workspaceService.getWorkspaces ()[0].id
        }
        loadTasksForWorkspace (workspaceId)
    }
    def saveLastWorkspace() {
        dictionaryService.doSaveLong(LAST_WORKSPACE, workspaceId)
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
        public Comparator getComparator(int arg0) {
            return null
//            @SuppressWarnings("unchecked")
//            final Comparator comparator = GlazedLists.chainComparators(
////                    GlazedLists.beanPropertyComparator(Location.class, "continent"),
////                    GlazedLists.beanPropertyComparator(Location.class, "country"),
////                    GlazedLists.beanPropertyComparator(Location.class, "province"),
////                    GlazedLists.beanPropertyComparator(Location.class, "city")
//            );
//
//            return comparator;
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
