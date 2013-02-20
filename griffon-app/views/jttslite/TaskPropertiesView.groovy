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

    action(okAction,
            name: app.getMessage('application.action.OK.name', 'Ok'),
            shortDescription: app.getMessage('application.action.Ok.short_description', 'Ok')
    )
}

panel(id: 'content') {
    migLayout layoutConstraints: 'fill'
    label(text:"Name", constraints: 'gap para')
    textField(constraints: 'growx, wrap')

    label(text:"Description", constraints: 'gap para')
    textArea(constraints: 'growx, wrap')

    button(okAction, constraints: 'right')
    button(hideAction, constraints: 'right')

    keyStrokeAction(component: current,
            keyStroke: 'ESCAPE',
            condition: 'in focused window',
            action: hideAction)
}
