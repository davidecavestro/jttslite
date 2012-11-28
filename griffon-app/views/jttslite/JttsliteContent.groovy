package jttslite

//import org.viewaframework.widget.swing.table.*


import net.miginfocom.swing.MigLayout
import org.jfree.chart.ChartPanel
import org.viewaframework.swing.DynamicTable

import java.awt.BorderLayout
import javax.swing.JSplitPane
import javax.swing.tree.DefaultTreeCellRenderer
import ca.odell.glazedlists.TreeList
import javax.swing.tree.TreeCellRenderer

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
                    def delRenderer = new DefaultTreeCellRenderer()

                    def taskTree = jxtree( id: "tasks" , editable: true, cellRenderer: [
                            getTreeCellRendererComponent: {tree, value, selected, expanded, leaf, row, focus ->
                                if (value instanceof TreeList.Node)
                                    value = value.element.bean[value.element.property]
                                delRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, focus)
                            }] as TreeCellRenderer) {
                        eventTreeModel (source: model.tasks) {
                            afterEdit { path, node, value ->
                                node.element.bean[node.element.property] = value
                            }
                        }
                    }

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
