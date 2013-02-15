package jttslite

import static griffon.util.GriffonApplicationUtils.*

menuBar = menuBar {
    //File
    menu(text: app.getMessage('application.menu.File.name',' File'),
         mnemonic: app.getMessage('application.menu.File.mnemonic', 'F')) {
        menuItem(newAction)
        menuItem(openAction)
        separator()
        menuItem(editWorkspacesAction)
        separator()
        menuItem(saveAction)
        menuItem(saveAsAction)
        if(!isMacOSX) {
            separator()
            menuItem(action:quitAction, icon:fatcowIcon(icon:'door_out', size:16))
        }
    }

    //Edit
    menu(text: app.getMessage('application.menu.Edit.name', 'Edit'),
         mnemonic: app.getMessage('application.menu.Edit.mnemonic', 'E')) {
        menuItem(action:undoAction, icon:fatcowIcon(icon:'arrow_undo', size:16))
        menuItem(action:redoAction, icon:fatcowIcon(icon:'arrow_redo', size:16))
        separator()
        menuItem(action:cutAction, icon:fatcowIcon(icon:'cut', size:16))
        menuItem(action:copyAction, icon:fatcowIcon(icon:'page_white_copy', size:16))
        menuItem(action:pasteAction, icon:fatcowIcon(icon:'paste_plain', size:16))
        menuItem(action:deleteAction, icon:fatcowIcon(icon:'delete', size:16))
    }

    //View
    menu(text: app.getMessage('application.menu.View.name', 'View'),
         mnemonic: app.getMessage('application.menu.View.mnemonic', 'V')) {
    }

    //Tasks
    menu(text: app.getMessage('application.menu.Tasks.name', 'Tasks'),
        mnemonic: app.getMessage('application.menu.Tasks.mnemonic', 'T')) {
    }

    //Actions
    menu(text: app.getMessage('application.menu.Actions.name', 'Actions'),
        mnemonic: app.getMessage('application.menu.Actions.mnemonic', 'A')) {
    }

    //Tools
    menu(text: app.getMessage('application.menu.Tools.name', 'Tools'),
        mnemonic: app.getMessage('application.menu.Tools.mnemonic', 'o')) {
    }

    if(!isMacOSX) glue()  
    menu(text: app.getMessage('application.menu.Help.name', 'Help'),
         mnemonic: app.getMessage('application.menu.Help.mnemonic', 'H')) {
        if(!isMacOSX) {
            menuItem(aboutAction)
            menuItem(preferencesAction)
        }
    }
}