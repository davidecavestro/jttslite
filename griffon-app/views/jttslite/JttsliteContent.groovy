package jttslite

import ca.odell.glazedlists.swing.TreeTableSupport

//import org.viewaframework.widget.swing.table.*


import net.miginfocom.swing.MigLayout
import org.jfree.chart.ChartPanel
import org.viewaframework.swing.DynamicTable

import javax.swing.JTable
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import java.awt.BorderLayout
import javax.swing.JSplitPane
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.TreeCellRenderer
import javax.swing.ListSelectionModel
import org.jdesktop.swingx.treetable.FileSystemModel

import java.awt.Component

//panel {
//    migLayout layoutConstraints: 'fill'
//    splitPane {
//        scrollPane (constraints: 'left'){
//            jxtree( id: "topics" )
//        }
//        scrollPane (constraints: 'right'){
//            jxtree( id: "topics" )
//        }
//    }
//}
/* -------------------------------------------------- */
/* -------------------- MENU BAR -------------------- */
/* -------------------------------------------------- */
//menuBar{
//    menu('Application'){
//        menuItem(text:'Exit',icon:imageIcon('/griffon-icon-16x16.png'),
//                actionPerformed:controller.exitApplication)
//    }
//}
/* -------------------------------------------------- */
/* -------------------- LAYOUT ---------------------- */
/* -------------------------------------------------- */
splitPane {
    splitPane(id:'searchManagerPanel',orientation:JSplitPane.VERTICAL_SPLIT){
        dockingFrame(preferredSize: new java.awt.Dimension(300,300)) {
            panel(title:'Search Criteria'){
                borderLayout()
                panel(name:'mainPanel',layout: new MigLayout("fill"),constraints:java.awt.BorderLayout.NORTH){
//                    def delRenderer = new DefaultTreeCellRenderer()

//                    def taskTree = jxtree( id: "tasks", rootVisible: false, editable: true, cellRenderer: [
//                            getTreeCellRendererComponent: {tree, value, selected, expanded, leaf, row, focus ->
////                                if (value instanceof TreeList.Node)
////                                    value = value.element.bean[value.element.property]
//                                delRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, focus)
//                            }] as TreeCellRenderer) {
//                        eventTreeModel (source: model.tasks) {
//                            afterEdit { path, node, value ->
//                                node.element.bean[node.element.property] = value
//                            }
//                        }
//                    }

                    noparent {
                        selectionModel = new ca.odell.glazedlists.swing.EventSelectionModel(model.taskTreeList)
                        selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
                    }
                    def taskTreeTable = new JTable() {
                        Component prepareRenderer() {
                            super.prepareRenderer ()
                        }
                    }

                    scrollPane {
                        table(taskTreeTable, id:'taskTree', selectionModel:selectionModel) {
                            tableFormat = defaultAdvancedTableFormat(columns: [
                                    [name: 'title',     title: 'Name'],
                                    [name: 'treeCode',     title: 'Tree code'],
                                    [name: 'description', title: 'Description']])
                            eventTableModel(source:model.taskList, format:tableFormat)
                            TreeTableSupport treeTableSupport = installTreeTableSupport(source:model.taskTreeList, index:0i)

                            treeTableSupport.arrowKeyExpansionEnabled = true
                            treeTableSupport.showExpanderForEmptyParent = true
                            /*
                            current.selectionModel.addListSelectionListener( [valueChanged: {ListSelectionEvent evt ->
                                if ( !evt.isAdjusting) {

                                    def task = model.taskList[evt.source.leadSelectionIndex]
                                    model.selectedTaskId = task.id
                                    //... do stuff with the selected index ...
                                }
                            }] as ListSelectionListener)
                            */
                        }
                    }

////                    scrollPane {
//                        treeTable(
//                            treeTableModel: new FileSystemModel(),
//                            showHorizontalLines: true,
//                            showVerticalLines: true
//                        )
////                    }

//                            id:'tasks', selectionModel:selectionModel) {
//                        tableFormat = defaultTableFormat(columnNames:["Name"])
//                        eventTableModel(source:model.tasks, format:tableFormat)
//                        installTreeTableSupport(source:model.tasks)
//                    }



//                    def delEditor = new MyEditor(taskTree, delRenderer)
//                    taskTree.setCellEditor(delEditor)
                }
            }
        }
        dockingFrame{
            panel(id:'chartPanel',name:'chartPanel',title:'Search Statistics'){
                widget(new ChartPanel(chart))
            }
        }
    }
    dockingFrame{
        panel(title:'Search Results'){
            borderLayout()
            busyComponent(id: "c1", constraints: CENTER/*, busy: bind(source:model,sourceProperty:'searchResultsPanelEnabled')*/) {
                busyModel(description: "Please Wait...")
                scrollPane(name:'peopleTableWrapper',constraints:BorderLayout.CENTER) {
                    widget(new DynamicTable(model.tableModel))
                }
            }
        }
    }
}
