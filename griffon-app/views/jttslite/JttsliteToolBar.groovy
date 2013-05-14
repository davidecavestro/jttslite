package jttslite

menuBar = toolBar {
    button (icon:fatcowIcon(icon:'package_add', size:32))
    button (icon:fatcowIcon(icon:'add', size:32))
    button (icon:fatcowIcon(icon:'package_go', size:32))
    button (icon:fatcowIcon(icon:'clock_add', size:32))
    button (action: startWorkLogAction, icon:fatcowIcon(icon:'clock_play', size:32))
    button (action: stopWorkLogAction, icon:fatcowIcon(icon:'clock_stop', size:32))
    separator()
    button (action: deleteTaskAction)
    button (action: deleteWorklogAction)
    separator()
    button (icon:fatcowIcon(icon:'cut', size:32))
    button (icon:fatcowIcon(icon:'page_white_copy', size:32))
    button (icon:fatcowIcon(icon:'paste_plain', size:32))
    button (icon:fatcowIcon(icon:'arrow_undo', size:32))
    button (icon:fatcowIcon(icon:'arrow_redo', size:32))
    separator()
    button (icon:fatcowIcon(icon:'printer', size:32))
    button (icon:fatcowIcon(icon:'package', size:32))
    button (icon:fatcowIcon(icon:'clock', size:32))
    button (icon:fatcowIcon(icon:'help', size:32))
}