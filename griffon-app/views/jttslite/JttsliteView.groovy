package jttslite

actions {
    action(
            id: "exitAction",
            name: "Exit",
            closure: {System.exit(0)}
    )
    action(
            id: "startWorkLogAction",
            name: "Start",
            enabled: bind {!model.inProgress},
            closure: controller.&startProgress
    )
    action(
            id: "stopWorkLogAction",
            name: "Stop",
            enabled: bind {model.inProgress},
            closure: controller.&stopProgress
    )
}
application(title: 'jttslite',
        preferredSize: [640, 480],
        pack: true,
        //location: [50,50],
        locationByPlatform: true,
        iconImage: imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                imageIcon('/griffon-icon-32x32.png').image,
                imageIcon('/griffon-icon-16x16.png').image]) {
    // add content here
    vbox {
        menuBar {
            menu ('File') {
                menuItem exitAction
            }
        }
        toolBar {
            button (action: startWorkLogAction)
            button (action: stopWorkLogAction)
        }
        splitPane {
            panel {
                tree {}
            }
            panel {
                table {}
            }
        }
        //statusBar {}
    }
}
