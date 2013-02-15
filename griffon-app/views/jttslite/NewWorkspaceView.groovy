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
}

panel(id: 'content') {
    label(text:"Name", constraints: 'wrap')
    textField(constraints: 'wrap')

    label(text:"Description", constraints: 'wrap')
    textField(constraints: 'wrap')

    label(text:"Open automatically", constraints: 'wrap')
    checkBox()

    keyStrokeAction(component: current,
            keyStroke: 'ESCAPE',
            condition: 'in focused window',
            action: hideAction)
}
