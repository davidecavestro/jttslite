log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%d [%t] %-5p %c - %m%n')
    }

    error  'org.codehaus.griffon'

    info   'griffon.util',
           'griffon.core',
           'griffon.swing',
           'griffon.app'
}


swing {
    windowManager {
        defaultHandler = new jttslite.JttsliteWindowDisplayHandler()
    }
}


i18n.basename = 'messages'

griffon.services.basic.disabled = true

griffon.datasource.injectInto = ["controller", "service"]