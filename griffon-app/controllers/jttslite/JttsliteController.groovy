package jttslite

import griffon.swing.SwingApplication

import javax.swing.JFrame

import griffon.transform.Threading

class JttsliteController {

    WorkspaceService workspaceService
    TaskService taskService
    WorklogService worklogService
    DictionaryService dictionaryService

    JttsliteModel model
    JttsliteView view

    def newAction = {
    }

    def openAction = {
    }

    def saveAction = {
    }

    def saveAsAction = {
    }

    def aboutAction = {
        withMVCGroup('about') { m, v, c ->
            c.show()
        }
    }

    def preferencesAction = {
        withMVCGroup('preferences') { m, v, c ->
            c.show()
        }
    }

    def quitAction = {
        app.shutdown()
    }

    def undoAction = {
    }

    def redoAction = {
    }

    def cutAction = {
    }

    def copyAction = {
    }

    def pasteAction = {
    }

    def deleteAction = {
    }

    def onOSXAbout = { app ->
        withMVCGroup('about') { m, v, c ->
            c.show()
        }
    }

    def onOSXQuit = { app ->
        quitAction()
    }

    def onOSXPrefs = { app ->
        withMVCGroup('preferences') { m, v, c ->
            c.show()
        }
    }

    // void mvcGroupInit(Map args) {
    //    // this method is called after model and view are injected
    // }

    // void mvcGroupDestroy() {
    //    // this method is called when the group is destroyed
    // }

    /*
        Remember that actions will be called outside of the UI thread
        by default. You can change this setting of course.
        Please read chapter 9 of the Griffon Guide to know more.

    def action = { evt = null ->
    }
    */
    def startProgress = { evt = null ->
        app.event('StartProgress')
    }
    def stopProgress = { evt = null ->
        app.event('StopProgress')
    }

    def onStartProgress = { evt = null ->
        model.inProgressWorklogId = worklogService.doStart(model.selectedTaskId)

        model.worklogList.add (worklogService.getWorklog(model.inProgressWorklogId) as ObservableMap)
    }
    def onStopProgress = { evt = null ->
        worklogService.doStop(model.inProgressWorklogId)
        model.worklogMap[model.inProgressWorklogId] = worklogService.getWorklog(model.inProgressWorklogId) as ObservableMap
        model.inProgressWorklogId = null
    }

    def onStartupEnd = {SwingApplication app->
        /*
        eventually loads initial/default data
         */
        switch(Environment.current) {
            case Environment.DEVELOPMENT:
                configureForDevelopment()
                break
            case Environment.PRODUCTION:
                configureForProduction()
                break
        }
        loadData ()

        def window = app.windowManager.startingWindow
        window.extendedState |= JFrame.MAXIMIZED_BOTH
    }

    def onShutdownRequested = {app->
        saveLastWorkspace()
    }

/*
def whenSpringReadyEnd = {app, applicationContext->
}*/

    def loadData = { evt = null ->
        app.event('DataLoad')
    }

    def onDataLoad = { evt = null ->
        loadLastWorkspace ()
    }

    def configureForProduction() {
        if (!workspaceService.workspacesCount ()) {
            griffon.plugins.splash.SplashScreen.instance.showStatus(app.getMessage ('application.splash.GeneratingDefaultData', "Generating default data"))
            def workspaceId = workspaceService.doInsert ("Default", "Default workspace")
            def rootId = taskService.doInsert (workspaceId, null, 'Default workspace')
        }
    }

    def configureForDevelopment() {
        if (!workspaceService.workspacesCount ()) {
            griffon.plugins.splash.SplashScreen.instance.showStatus(app.getMessage ('application.splash.GeneratingDefaultData', "Generating default data"))
            def workspaceId = workspaceService.doInsert ("Default", "Default workspace")

            def rootId = taskService.doInsert (workspaceId, null, 'Task 1', 'Task 1 (Default worskpace)')
            def task11Id = taskService.doInsert(workspaceId, rootId, 'Task 1.1')
            def task12Id = taskService.doInsert(workspaceId, rootId, 'Task 1.2')
            def task111Id = taskService.doInsert(workspaceId, task11Id, 'Task 1.1.1')
        }
    }

    def loadTasksForWorkspace(def workspaceId) {
        loadTasks (taskService.getTasks(workspaceId).collect {it as ObservableMap})
        loadRunningWorklog (workspaceId)
    }

    def loadRunningWorklog(def workspaceId) {
        model.inProgressWorklogId = worklogService.getRunningWorklog (workspaceId)?.id
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    def loadTasks(def tasks) {
        model.taskList.addAll (tasks)
    }

    /**
     * Load the last workspace loaded (upon restart). If this is the first run the last workspace loaded is no more
     * available loads the first available workspace.
     *
     * <p>
     *     Loading a workspace implies loading its data into application UI.
     * </p>
     */
    def loadLastWorkspace() {
        griffon.plugins.splash.SplashScreen.instance.showStatus(app.getMessage ('application.splash.LoadingLastWorkspace', "Loading last workspace used"))
        model.workspaceId = dictionaryService.getLongValue(LAST_WORKSPACE)
        if (model.workspaceId==null || !workspaceService.getWorkspace (model.workspaceId)) {
            /*
            if there's no last workspace memory or it is no more available
            load the first available workspace
             */
            model.workspaceId = workspaceService.getWorkspaces ()[0].id
        }
        loadTasksForWorkspace (model.workspaceId)
    }

    private final static String LAST_WORKSPACE = 'lastWorkspace'

    def saveLastWorkspace() {
        dictionaryService.doSaveLong(LAST_WORKSPACE, model.workspaceId)
    }

    def renameTask (def taskId, def newName) {
        taskService.doRename(taskId, newName)
        model.taskMap[taskId].title = newName
    }
}
