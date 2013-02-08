package jttslite

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

    action(createWorkspaceAction,
            name: app.getMessage('application.action.New.name', 'New'),
            mnemonic: app.getMessage('application.action.New.mnemonic', 'N'),
            shortDescription: app.getMessage('application.action.New.short_description', 'New workspace')
    )

    action(deleteWorkspaceAction,
            name: app.getMessage('application.action.Delete.name', 'Delete'),
            mnemonic: app.getMessage('application.action.Delete.mnemonic', 'D'),
            shortDescription: app.getMessage('application.action.Delete.short_description', 'Delete workspace')
    )

    action(openWorkspaceAction,
            name: app.getMessage('application.action.Open.name', 'Open'),
            mnemonic: app.getMessage('application.action.Open.mnemonic', 'O'),
            shortDescription: app.getMessage('application.action.Open.short_description', 'Open workspace')
    )

    action(exportWorkspaceAction,
            name: app.getMessage('application.action.Export.name', 'Export'),
            mnemonic: app.getMessage('application.action.Export.mnemonic', 'E'),
            shortDescription: app.getMessage('application.action.Export.short_description', 'Export')
    )

    action(importWorkspaceAction,
            name: app.getMessage('application.action.Import.name', 'Import'),
            mnemonic: app.getMessage('application.action.Import.mnemonic', 'I'),
            shortDescription: app.getMessage('application.action.Import.short_description', 'Import')
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

        def workspaceTable = new JTable()
        table(workspaceTable, id:'workspaceTable', selectionModel:selectionModel) {
            tableFormat = defaultAdvancedTableFormat(columns: [
                    [name: 'name',     title: 'Name']
                    ])
            eventTableModel(source:model.swingProxyWorkspaceList, format:tableFormat)

            current.selectionModel.addListSelectionListener( [valueChanged: {ListSelectionEvent evt ->
                if ( !evt.isAdjusting) {

                    def selectionIndex = evt.source.leadSelectionIndex

                    if (selectionIndex!=null && selectionIndex>=0) {
                        //... do stuff with the selected index ...
                    } else {
                    }
                }
            }] as ListSelectionListener)

        }
    }

    button(createWorkspaceAction, constraints: 'right')
    button(openWorkspaceAction, constraints: 'right')
    button(deleteWorkspaceAction, constraints: 'right')
    button(exportWorkspaceAction, constraints: 'right')
    button(importWorkspaceAction, constraints: 'right')
    button(hideAction, constraints: 'right')

    keyStrokeAction(component: current,
            keyStroke: 'ESCAPE',
            condition: 'in focused window',
            action: hideAction)
}
