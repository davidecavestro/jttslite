package jttslite

import static griffon.util.GriffonApplicationUtils.*

menuBar = menuBar {
    //File
    menu(text: app.getMessage('application.menu.File.name',' File'),
         mnemonic: app.getMessage('application.menu.File.mnemonic', 'F')) {
        menuItem(newWorkspaceAction)
        menuItem(newTaskAction)
        separator()
        menuItem(openAction, icon:fatcowIcon(icon:'package_go', size:16))
        menuItem(text: 'Export Workspace', icon:fatcowIcon(icon:'document_export', size:16))
        menuItem(text: 'Import Workspace', icon:fatcowIcon(icon:'document_import', size:16))
        separator()
        menuItem(editWorkspacesAction, icon:fatcowIcon(icon:'package', size:16))
        separator()
        menuItem(saveAction)
        menuItem(saveAsAction)
        separator()
        menuItem(text: 'Reporting', icon:fatcowIcon(icon: 'printer', size:16))
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
        menuItem(text: 'Rename', icon:fatcowIcon(icon:'textfield_rename', size:16))
        menuItem(action: taskPropertiesAction, icon:fatcowIcon(icon:'document_properties', size:16))
    }

    //Actions
    menu(text: app.getMessage('application.menu.Actions.name', 'Actions'),
        mnemonic: app.getMessage('application.menu.Actions.mnemonic', 'A')) {
        menuItem(text: 'Add action', icon:fatcowIcon(icon:'clock_add', size:16))
        menuItem(text: 'Start action', icon:fatcowIcon(icon:'clock_play', size:16))
        menuItem(text: 'Start action clone', icon:fatcowIcon(icon:'clock_go', size:16))
        menuItem(text: 'Continue action', icon:fatcowIcon(icon:'clock_play', size:16))
        separator()
        menuItem(text: 'Stop action', icon:fatcowIcon(icon:'clock_stop', size:16))
    }

    //Tools
    menu(text: app.getMessage('application.menu.Tools.name', 'Tools'),
        mnemonic: app.getMessage('application.menu.Tools.mnemonic', 'o')) {
        menuItem(text: 'Log console')
        menuItem(text: 'Options', icon:fatcowIcon(icon:'setting_tools', size:16))
        menuItem(text: 'Workspaces', icon:fatcowIcon(icon:'package', size:16))
        menuItem(text: 'Templates', icon:fatcowIcon(icon:'clock', size:16))
    }

    if(!isMacOSX) glue()  
    menu(text: app.getMessage('application.menu.Help.name', 'Help'),
         mnemonic: app.getMessage('application.menu.Help.mnemonic', 'H')) {
        if(!isMacOSX) {
            menuItem(action:aboutAction, icon: fatcowIcon(icon:'information', size: 16))
            menuItem(action:preferencesAction, icon: fatcowIcon(icon:'cog', size: 16))
        }
    }
}