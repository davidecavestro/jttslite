package jttslite

import java.awt.Color
import java.awt.Font
import org.jfree.chart.labels.PieToolTipGenerator
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.data.general.DefaultPieDataset

build(JttsliteActions)

/* Setting some variables */
piedataset = new DefaultPieDataset()
/* Setting default chart values */
piedataset.with {
    setValue "No Match", 1
    setValue "Match", 10
}
options = [true, true, true]
chart = ChartFactory.createPieChart("",piedataset, *options)
chart.backgroundPaint = Color.white

application(title: GriffonNameUtils.capitalize(app.getMessage('application.title', app.config.application.title)),
        pack: true,
        locationByPlatform: true,
        iconImage: imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                imageIcon('/griffon-icon-32x32.png').image,
                imageIcon('/griffon-icon-16x16.png').image]) {
    widget(build(JttsliteMenuBar))
    migLayout(layoutConstraints: 'fill')
    widget(build(JttsliteToolBar), constraints: 'north, grow')
    widget(build(JttsliteContent), constraints: 'center, grow')
    widget(build(JttsliteStatusBar), constraints: 'south, grow')
}
