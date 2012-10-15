package jttslite

class JttsliteModel {
    @Bindable boolean inProgress
    @Bindable String status

    void mvcGroupInit(Map args) {
        status = "Welcome to ${GriffonNameUtils.capitalize(app.getMessage('application.title'))}"
    }
}
