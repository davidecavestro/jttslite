package jttslite

import ca.odell.glazedlists.swing.TreeTableSupport
import net.miginfocom.swing.MigLayout

import org.jfree.chart.ChartPanel

import javax.swing.*
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import java.awt.*

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
    splitPane(id:'mainPanel',orientation:JSplitPane.VERTICAL_SPLIT){
        dockingFrame(preferredSize: new java.awt.Dimension(300,300)) {
            panel(title:'Tasks'){
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
                            eventTableModel(source:model.taskTreeList, format:tableFormat)
                            TreeTableSupport treeTableSupport = installTreeTableSupport(source:model.taskTreeList, index:0i)

                            treeTableSupport.arrowKeyExpansionEnabled = true
                            treeTableSupport.showExpanderForEmptyParent = true

                            current.selectionModel.addListSelectionListener( [valueChanged: {ListSelectionEvent evt ->
                                if ( !evt.isAdjusting) {

                                    def selectionIndex = evt.source.leadSelectionIndex

                                    if (selectionIndex!=null && selectionIndex>=0) {
                                        def task = model.taskList[selectionIndex]
                                        //controller.selectedTaskChanged (task.id)
                                        model.selectedTaskId = task.id
                                        //... do stuff with the selected index ...
                                    } else {
                                        model.selectedTaskId = null
                                        //controller.selectedTaskChanged (null)
                                    }
                                }
                            }] as ListSelectionListener)

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
            panel(id:'chartPanel',name:'chartPanel',title:'Charts'){
                widget(new ChartPanel(chart))
            }
        }
    }
    dockingFrame{
        panel(title:'Worklogs'){
            borderLayout()
            busyComponent(id: "c1", constraints: CENTER/*, busy: bind(source:model,sourceProperty:'searchResultsPanelEnabled')*/) {
                busyModel(description: "Please Wait...")
                scrollPane(name:'worklogTableWrapper',constraints:BorderLayout.CENTER) {

                    def selectionModel
                    //widget(new DynamicTable(model.tableModel))
                    noparent {
                        selectionModel = new ca.odell.glazedlists.swing.EventSelectionModel(model.swingProxyWorklogList)
                        selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
                    }
                    def worklogTable = new JTable() {
                        Component prepareRenderer() {
                            super.prepareRenderer ()
                        }
                    }
                    table(worklogTable, id:'worklogTable', selectionModel:selectionModel) {
                        tableFormat = defaultAdvancedTableFormat(columns: [
                                [name: 'start',     title: 'Start'],
                                [name: 'amount',     title: 'Duration'],
                                [name: 'comment', title: 'Comment']])
                        eventTableModel(source:model.swingProxyWorklogList, format:tableFormat)

                        current.selectionModel.addListSelectionListener( [valueChanged: {ListSelectionEvent evt ->
                            if ( !evt.isAdjusting) {

                                def selectionIndex = evt.source.leadSelectionIndex

                                if (selectionIndex!=null && selectionIndex>=0) {
                                    //... do stuff with the selected index ...
                                } else {
                                }
                            }
                        }] as ListSelectionListener)

                    }

                }
            }
        }
    }
}
