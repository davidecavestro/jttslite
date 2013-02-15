package jttslite

import ca.odell.glazedlists.*
import ca.odell.glazedlists.swing.GlazedListsSwing

class NewWorkspaceModel extends AbstractDialogModel {

    @Override
    protected String getDialogKey() {
        return "NewWorkspace"
    }

    @Override
    protected String getDialogTitle() {
        return "NewWorkspace"
    }

    void mvcGroupInit(Map<String, Object> args) {
        super.mvcGroupInit(args)
        width = 600
        height = 320
    }

}
