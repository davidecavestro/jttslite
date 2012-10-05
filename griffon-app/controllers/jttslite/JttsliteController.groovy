package jttslite

class JttsliteController {
    // these will be injected by Griffon
    def model
    def view

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
    def startWorkLog = { evt = null ->
        app.event('StartWorkLog')
    }
    def stopWorkLog = { evt = null ->
        app.event('StopWorkLog')
    }
    def onStartWorkLog = { evt = null ->
        model.loggingWork = true
        println(model.loggingWork)
    }
    def onStopWorkLog = { evt = null ->
        model.loggingWork = false
        println(model.loggingWork)
    }
}
