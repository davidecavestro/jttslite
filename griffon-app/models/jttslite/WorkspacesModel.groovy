package jttslite

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.DisposableMap
import ca.odell.glazedlists.EventList
import ca.odell.glazedlists.FunctionList
import ca.odell.glazedlists.GlazedLists
import ca.odell.glazedlists.ObservableElementList
import ca.odell.glazedlists.swing.GlazedListsSwing

class WorkspacesModel extends AbstractDialogModel {

    WorkspaceService workspaceService

    /**
     * the workspaces list
     */
    EventList<WorkspaceBean> workspaceList = GlazedLists.threadSafeList(new BasicEventList ())
    ObservableElementList<WorkspaceBean> observableWorkspaceList = new ObservableElementList (workspaceList, GlazedLists.beanConnector (WorkspaceBean.class))
    DisposableMap<Long, WorkspaceBean> workspaceMap = GlazedLists.syncEventListToMap(workspaceList, new WorkspaceKeyMaker ())
    EventList<WorkspaceBean> swingProxyWorkspaceList = GlazedListsSwing.swingThreadProxyList(observableWorkspaceList)
    Long selectedWorkspaceId

    @Override
    protected String getDialogKey() {
        return "Workspaces"
    }

    @Override
    protected String getDialogTitle() {
        return "Workspaces"
    }

    public void setSelectedWorkspaceId (Long workspaceId) {
        selectedWorkspaceId = workspaceId
    }

    void mvcGroupInit(Map<String, Object> args) {
        super.mvcGroupInit(args)
        width = 600
        height = 320
    }

    class WorkspaceKeyMaker implements FunctionList.Function<WorkspaceBean, Long> {

        Long evaluate(WorkspaceBean sourceValue) {
            return sourceValue.id
        }
    }
}
