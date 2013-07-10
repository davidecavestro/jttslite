/*
 * Copyright (c) 2013, the original author or authors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package jttslite

import griffon.swing.SwingApplication
import groovy.time.TimeCategory
import groovy.time.TimeDuration

import javax.swing.JFrame

import griffon.transform.Threading

/**
 * The main controller
 */
class JttsliteController {

    WorkspaceService workspaceService
    TaskService taskService
    WorklogService worklogService
    DictionaryService dictionaryService

    JttsliteModel model
    JttsliteView view

    /**
     * The timer used to reflect working log related changes
     */
    TimerTask updateRunningTicTask

    def newWorkspaceAction = {
    }

    def newTaskAction = {
    }

    def openAction = {
        withMVCGroup('openWorkspace') { m, v, c ->
            c.show()
        }
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
        app.event('deleteTriggered')
    }

    def deleteTaskAction = {
        List toDelete = new ArrayList (model.taskSelection)
        if (toDelete) {
            taskService.doDelete(toDelete.collect {it.id})
            //remove items from model
            model.taskList.removeAll(toDelete)
        }
    }

    def deleteWorklogAction = {
        List toDelete = new ArrayList (model.worklogSelection)
        if (toDelete) {
            worklogService.doDelete(toDelete.collect {it.id})
            //remove items from model
            model.worklogList.removeAll(toDelete)
        }
    }

    def editWorkspacesAction = {
        withMVCGroup('workspaces') { m, v, c ->
            c.show()
        }
    }

    def taskPropertiesAction = {
        withMVCGroup('taskProperties') { m, v, c ->
            c.show()
        }
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
        model.workingLogId = worklogService.doStart(model.selectedTaskId)
        startTimer ()

        //add the new worklog
        model.worklogList.add (worklogService.getWorklog(model.workingLogId))
        view.systemTray.trayIcons[0].toolTip = app.getMessage ('application.tray.Running', "Running")
    }
    def onStopProgress = { evt = null ->
        def workingLogId = model.workingLogId

        worklogService.doStop(workingLogId)
        //refresh the stopped worklog
        model.worklogMap[workingLogId] = worklogService.getWorklog(workingLogId)

        model.workingLogId = null
        stopTimer ()
        //refresh tasks above the stopped worklog
        onWorkingTic (model.worklogMap[workingLogId])
        /*
        taskService.getTaskPathIds().each {taskId->
            model.taskMap[taskId] = taskService.getTask(taskId)
        }
        */
        view.systemTray.trayIcons[0].toolTip = app.getMessage ('application.tray.Idle', "Idle")
    }

    def onStartupEnd = {SwingApplication app->
        griffon.plugins.splash.SplashScreen.instance.showStatus(app.getMessage ('application.splash.ConfiguringEnvironment', "Configuring environment"))
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

    def loadData() {
        loadLastWorkspace()
        app.event('DataLoad')
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

    def loadTasksForWorkspace(long workspaceId) {
        loadTasks (taskService.getTasks(workspaceId))
        loadWorkingLog (workspaceId)
    }

    def loadWorkingLog(long workspaceId) {
        model.workingLogId = worklogService.getWorkingLog (workspaceId)?.id
        startTimer ()
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    void loadTasks(Collection<TaskBean> tasks) {
        model.taskList.addAll (tasks)
    }

    /**
     * Load the last workspace loaded (upon restart). If this is the first run or the last workspace loaded is no more
     * available, then loads the first available workspace.
     *
     * <p>
     *     Loading a workspace implies loading its data into application UI.
     * </p>
     */
    public void loadLastWorkspace() {
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

    /**
     * Renames a task.
     *
     * @param taskId the task id
     * @param newName the new task name
     */
    void renameTask (long taskId, String newName) {
        taskService.doRename(taskId, newName)
        model.taskMap[taskId].title = newName
    }

    /**
     * Refreshes working log related data in order to reflect the working log current amount.
     *
     * @param timerTask the task that should be canceled when the worklog is stopped
     */
    void onWorkingTic(TicTimerTask timerTask) {
        if (model.working) {
            def workingLog = model.workingLog
            def amount = onWorkingTic (workingLog)

            def modelWorklog = model.worklogMap[workingLog.id]
            if (modelWorklog) {//the working log is actually visible: its amount should be incremented
//            modelWorklog.firePropertyUpdatedEvent ('amount', modelWorklog.amount, amount)
                modelWorklog.amount = amount
            }

            def runningDuration = DurationUtils.formatDuration(amount)
            view.systemTray.trayIcons[0].toolTip = app.getMessage ('application.tray.RunningWithAmount', [runningDuration], "Running ($runningDuration)".toString())
        } else {
            timerTask.cancel()
        }
    }

    Long onWorkingTic(WorklogBean workingLog) {
        def amount = workingLog.amount!=null?workingLog.amount:System.currentTimeMillis() - workingLog.start.time

        model.workingPathIds.each {taskId->
            def modelTask = model.taskMap[taskId]
            modelTask.localWorkingAmount = modelTask.localAmount + amount
            modelTask.globalWorkingAmount = modelTask.globalAmount + amount
        }

        return amount
    }

    Timer timer
    def startTimer () {
        timer = new Timer()
        updateRunningTicTask = new TicTimerTask()
        timer.schedule (updateRunningTicTask, 0l, 1000l) //tic once per second
    }
    def stopTimer () {
        updateRunningTicTask?.cancel ()
        timer?.purge()
    }

    /**
     * Repeatedly calls the working log refresh routine
     */
    class TicTimerTask extends TimerTask {
        @Override
        void run() {
            JttsliteController.this.onWorkingTic (this)
        }
    }
}
