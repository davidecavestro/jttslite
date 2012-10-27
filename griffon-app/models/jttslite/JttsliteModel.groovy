package jttslite

import org.viewaframework.widget.swing.table.*
import org.viewaframework.swing.table.*

class JttsliteModel {
    @Bindable boolean inProgress
    @Bindable String status


    /* Table result model using viewaframework.org DynamicTableModel */
    @Newify([DynamicTableColumn])
    def tableModel =
        new DynamicTableModel([
                DynamicTableColumn.new(propertyName:"name",order:0,width:400,title:"Start time"/*,renderer:FileNameRendererByType.new()*/),
                DynamicTableColumn.new(propertyName:"type",order:1,width:100,title:"Finish time"),
                DynamicTableColumn.new(propertyName:"size",order:2,width:100,title:"Duration"),
                DynamicTableColumn.new(propertyName:"path",order:3,width:600,title:"Comment")
        ])

    void mvcGroupInit(Map args) {
        status = "Welcome to ${GriffonNameUtils.capitalize(app.getMessage('application.title'))}"
    }
}
