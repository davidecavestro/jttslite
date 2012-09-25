application {
    title = 'Jttslite'
    startupGroups = ['jttslite']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "jttslite"
    'jttslite' {
        model      = 'jttslite.JttsliteModel'
        view       = 'jttslite.JttsliteView'
        controller = 'jttslite.JttsliteController'
    }

}
