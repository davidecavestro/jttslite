package jttslite

import ca.odell.glazedlists.EventList

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
    scrollPane(constraints: 'grow') {
        def selectionModel

        noparent {
            selectionModel = new ca.odell.glazedlists.swing.EventSelectionModel(model.swingProxyWorkspaceList)
            selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        }

        def workspaceTable = new JTable()
        table(workspaceTable, id:'workspaceTable', selectionModel:selectionModel) {
            tableFormat = defaultWritableTableFormat(columns: [
                        [name: 'name',     title: 'Name',  write: {baseObject, columnNames, index, editedValue->
                            baseObject.name =  editedValue
                            controller.updateWorkspace (baseObject.id, editedValue, baseObject.description)
                        }],
                        [name: 'description',     title: 'Description' ,  write: {baseObject, columnNames, index, editedValue->
                            baseObject.description =  editedValue
                            controller.updateWorkspace (baseObject.id, baseObject.name, editedValue)
                        }]
                    ],editable: {baseObject, columnNames, index->index==0})
            eventTableModel(source:model.swingProxyWorkspaceList, format:tableFormat)

            current.selectionModel.addListSelectionListener( [valueChanged: {ListSelectionEvent evt ->
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

    panel(id: 'buttonsPanel'){
        migLayout layoutConstraints: 'fill'
        button(createWorkspaceAction, constraints: 'growx, right, wrap')
        button(openWorkspaceAction, constraints: 'growx,right, wrap')
        button(deleteWorkspaceAction, constraints: 'growx,right, wrap')
        button(exportWorkspaceAction, constraints: 'growx,right, wrap')
        button(importWorkspaceAction, constraints: 'growx,right, wrap')
        button(hideAction, constraints: 'growx, right, wrap')
    }


    keyStrokeAction(component: current,
            keyStroke: 'ESCAPE',
            condition: 'in focused window',
            action: hideAction)
}
