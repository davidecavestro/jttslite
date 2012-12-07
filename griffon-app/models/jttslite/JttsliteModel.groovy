package jttslite

//import org.viewaframework.widget.swing.table.*


import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.SortedList
import ca.odell.glazedlists.TreeList
import org.viewaframework.swing.table.DynamicTableColumn
import org.viewaframework.swing.table.DynamicTableModel
import ca.odell.glazedlists.GlazedLists
import ca.odell.glazedlists.EventList

class JttsliteModel {
    @Bindable boolean inProgress
    @Bindable String status
    @Bindable Long selectedTaskId
    @Bindable Long inProgressWorklogId

    TaskService taskService

    BasicEventList taskList = new BasicEventList ()
    TreeList tasks = new TreeList(new SortedList (taskList, {a,b-> a.key <=> b.key} as Comparator), new TaskTreeFormat(), TreeList.NODES_START_EXPANDED)
    EventList worklogs = new SortedList (new BasicEventList (), {a,b-> a.key <=> b.key} as Comparator)

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

    private class TaskTreeFormat implements TreeList.Format {
        public void getPath(List path, Object element) {
            path.addAll (taskService.getTaskPath (element.id))
        }

        public boolean allowsChildren(Object element) {
            return true;
        }

        @Override
        public Comparator getComparator(int arg0) {
            @SuppressWarnings("unchecked")
            final Comparator comparator = GlazedLists.chainComparators(
//                    GlazedLists.beanPropertyComparator(Location.class, "continent"),
//                    GlazedLists.beanPropertyComparator(Location.class, "country"),
//                    GlazedLists.beanPropertyComparator(Location.class, "province"),
//                    GlazedLists.beanPropertyComparator(Location.class, "city")
            );

            return comparator;
        }
    }
}
