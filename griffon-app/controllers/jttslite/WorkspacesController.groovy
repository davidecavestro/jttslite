package jttslite

import griffon.transform.Threading

import java.awt.*

class WorkspacesController {

    WorkspacesModel model
    WorkspaceService workspaceService

    def view
    def builder

    protected dialog

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    void show(Window window) {
        window = window ?: Window.windows.find{it.focused}
        if(!dialog || dialog.owner != window) {
            app.windowManager.hide(dialog)
            dialog = builder.dialog(
                owner: window,
                title: model.title,
                resizable: model.resizable,
                modal: model.modal) {
                container(view.content)
            }
            if(model.width > 0 && model.height > 0) {
                dialog.preferredSize = [model.width, model.height]
            }
            dialog.pack()
        }
        int x = window.x + (window.width - dialog.width) / 2
        int y = window.y + (window.height - dialog.height) / 2
        dialog.setLocation(x, y)
        app.windowManager.show(dialog)
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    def hide = { evt = null ->
        app.windowManager.hide(dialog)
        dialog = null
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    def createWorkspace = {
        execOutsideUI {
            int newDataId = workspaceService.doInsert("")
            def newData = workspaceService.getWorkspace(newDataId) as ObservableMap
            execInsideUIAsync {
                model.workspaceList.add(newData)
            }
        }
    }

    def updateWorkspace(def workspaceId, def newName, def newDescription) {
        workspaceService.doUpdate(workspaceId, newName, newDescription)
    }

    def openWorkspace = {
        //TODO da implementare
    }

    def deleteWorkspace = {
        def workspaceId = model.selectedWorkspaceId
        model.workspaceList.remove(workspaceService.getWorkspace(workspaceId))
        workspaceService.doDelete(workspaceId)
    }

    def exportWorkspace = {
        //TODO da implementare
    }

    def importWorkspace = {
        //TODO da implementare
    }
}
