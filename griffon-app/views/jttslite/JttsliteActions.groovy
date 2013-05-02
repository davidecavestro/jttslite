package jttslite

import griffon.plugins.fatcow.FatcowIconFactory

import javax.swing.KeyStroke

def deleteAction = new ProxyingSwingAction ()
app.addApplicationEventListener([
        TreeGainedFocus: {a->
//            deleteProxyingAction.wrap (treeDeleteAction)
            deleteAction.wrap (app.actionManager.actionFor (controller, 'deleteTaskAction'))
        },
        TreeLostFocus: {a->
            deleteAction.wrap (null)
        },
        TableGainedFocus: {a->
            deleteAction.wrap (app.actionManager.actionFor (controller, 'deleteWorklogAction'))
        },
        TableLostFocus: {a->
            deleteAction.wrap (null)
        },
        DeleteTriggered: {a->
            deleteAction.actionPerformed ()
        }
])
actions {
    action(saveAction, enabled: false)
    action(saveAsAction, enabled: false)
    action(undoAction, enabled: false)
    action(redoAction, enabled: false)
    action(cutAction, enabled: false)
    action(copyAction, enabled: false)
    action(pasteAction, enabled: false)
    action('deleteAction' ,
            accelerator: KeyStroke.getKeyStroke(app.getMessage('application.action.Delete.accelerator', 'meta DELETE')),
            enabled: false
    )//{deleteAction}

    action(
            id: "deleteTaskAction",
            name: "Delete tasks",
            enabled: bind {model.tasksSelected}
    )
    action(
            id: "deleteWorklogAction",
            name: "Delete worklogs",
            enabled: bind {model.worklogsSelected}
    )

    action(
            id: "startWorkLogAction",
            name: "Start",
            smallIcon: fatcowIcon(icon:"clock_play", size: 16),
            enabled: bind {model.startEnabled},
            closure: controller.&startProgress
    )

    action(
            id: "stopWorkLogAction",
            name: "Stop",
            smallIcon: fatcowIcon(icon:"clock_stop", size: 16),
            enabled: bind {model.stopEnabled},
            closure: controller.&stopProgress
    )

    action(editWorkspacesAction, enabled: true)
}

systemTray {
    trayIcon(id: "trayIcon",
            resource: "/jtts-icon-48x48.png",
            class: FatcowIconFactory,
            toolTip: "...",
            actionPerformed: {
                def window = app.windowManager.windows.first()
                window.visible = true
                window.toFront ()
            }) {
        popupMenu {
            menuItem(startWorkLogAction)
            menuItem(stopWorkLogAction)
            separator()
            menuItem(quitAction)
        }
    }
}