package jttslite

import static griffon.util.GriffonApplicationUtils.getIsMacOSX

menuBar = toolBar {
    button (action: startWorkLogAction)
    button (action: stopWorkLogAction)
}