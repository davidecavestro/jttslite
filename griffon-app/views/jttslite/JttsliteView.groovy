package jttslite

build(JttsliteActions)

application(title: GriffonNameUtils.capitalize(app.getMessage('application.title', app.config.application.title)),
        pack: true,
        locationByPlatform: true,
        iconImage: imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                imageIcon('/griffon-icon-32x32.png').image,
                imageIcon('/griffon-icon-16x16.png').image]) {
    widget(build(JttsliteMenuBar))
    widget(build(JttsliteToolBar))
    migLayout(layoutConstraints: 'fill')
    widget(build(JttsliteContent), constraints: 'center, grow')
    widget(build(JttsliteStatusBar), constraints: 'south, grow')
}
