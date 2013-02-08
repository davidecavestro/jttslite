package jttslite

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.DisposableMap
import ca.odell.glazedlists.EventList
import ca.odell.glazedlists.FunctionList
import ca.odell.glazedlists.GlazedLists
import ca.odell.glazedlists.ObservableElementList
import ca.odell.glazedlists.SortedList
import ca.odell.glazedlists.TreeList
import ca.odell.glazedlists.swing.GlazedListsSwing

class WorkspacesModel extends AbstractDialogModel {

    WorkspaceService workspaceService

    /**
     * the workspaces list
     */
    EventList workspaceList = GlazedLists.threadSafeList(new BasicEventList ())
    ObservableElementList observableWorkspaceList = new ObservableElementList (workspaceList, GlazedLists.beanConnector (ObservableMap.class))
    DisposableMap workspaceMap = GlazedLists.syncEventListToMap(workspaceList, new WorkspaceMaker ())
    EventList swingProxyWorkspaceList = GlazedListsSwing.swingThreadProxyList(observableWorkspaceList)

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
        workspaceList.clear()
        if (workspaceId!=null) {
            execOutsideUI {
                def newData = workspaceService.getWorkspace(workspaceId).collect {it as ObservableMap}
                execInsideUIAsync {
                    workspaceList.addAll(newData)
                }
            }
        }
    }

    void mvcGroupInit(Map<String, Object> args) {
        super.mvcGroupInit(args)
        width = 600
        height = 320
    }

    class WorkspaceMaker implements FunctionList.Function<Map, Object> {

        Object evaluate(Map sourceValue) {
            return sourceValue.id
        }
    }
}
