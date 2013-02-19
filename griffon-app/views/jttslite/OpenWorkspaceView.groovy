package jttslite

import ca.odell.glazedlists.EventList

import javax.swing.JList
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener

actions {
    action(hideAction,
            name: app.getMessage('application.action.Close.name', 'Close'),
            mnemonic: app.getMessage('application.action.Close.mnemonic', 'C'),
            shortDescription: app.getMessage('application.action.Close.short_description', 'Close')
    )

    action(openWorkspaceAction,
            name: app.getMessage('application.action.Open.name', 'Open'),
            mnemonic: app.getMessage('application.action.Open.mnemonic', 'O'),
            shortDescription: app.getMessage('application.action.Open.short_description', 'Open workspace')
    )
}

panel(id: 'content') {
    migLayout layoutConstraints: 'fill'
    scrollPane(constraints: 'grow, wrap') {
        def selectionModel

        noparent {
            selectionModel = new ca.odell.glazedlists.swing.EventSelectionModel(model.swingProxyWorkspaceList)
            selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        }

        def workspaceList = new JList()
        list(workspaceList, id: 'workspaceList', selectionModel: selectionModel){

            eventListModel(source:model.workspaceList)

            current.selectionModel.addListSelectionListener([valueChanged: {ListSelectionEvent evt ->
                if ( !evt.isAdjusting) {
                    def selectionIndex = evt.source.leadSelectionIndex
                    if (selectionIndex!=null && selectionIndex>=0) {
                        //cambio di selezione
                        EventList selected = selectionModel.getSelected()
                        model.setSelectedWorkspaceId(selected.get(0).id)
                    }
                }
            }] as ListSelectionListener)
        }
    }

    button(openWorkspaceAction, constraints: 'right')
    button(hideAction, constraints: 'right')

    keyStrokeAction(component: current,
            keyStroke: 'ESCAPE',
            condition: 'in focused window',
            action: hideAction)
}
