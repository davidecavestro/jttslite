log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%d [%t] %-5p %c - %m%n')
    }

    error  'org.codehaus.griffon'

    info   'griffon.util',
           'griffon.core',
           'griffon.@application.toolkit@',
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

griffon.liquibase.rootChangeLogPath = 'classpath:/migrations/RootChangelog.groovy'

i18n.basenames = ['messages']









i18n.provider = 'i18n'
