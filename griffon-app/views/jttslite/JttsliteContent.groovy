package jttslite

import ca.odell.glazedlists.event.ListEventListener
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
        dockingFrame(preferredSize: new java.awt.Dimension(300,400)) {
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
                        selectionModel = new ca.odell.glazedlists.swing.EventSelectionModel(model.taskTreeList)//see https://sites.google.com/site/glazedlists/documentation/faq#TOC-JLists-JTables
                        selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
                    }
                    def taskTreeTable = new JTable() {
                        Component prepareRenderer() {
                            super.prepareRenderer ()
                        }
                    }

                    scrollPane {
                        table(taskTreeTable, id:'taskTree', selectionModel:selectionModel) {
                            tableFormat = defaultWritableTableFormat(columns: [
                                    [name: 'title', title: 'Name', write: {baseObject, columnNames, index, editedValue->
                                        //mantain selection in case of leaf nodes, see http://glazedlists.1045722.n5.nabble.com/TreeList-fires-insert-delete-event-on-update-JTable-selection-lost-td3418617.html
                                        controller.renameTask (baseObject.id, editedValue)
                                    }],
                                    [name: 'localAmount', title: 'Local amount'],
                                    [name: 'globalAmount', title: 'Subtree amount']],
                            editable: {baseObject, columnNames, index->index==0})
                            eventTableModel(source:model.taskTreeList, format:tableFormat)
                            TreeTableSupport treeTableSupport = installTreeTableSupport(source:model.taskTreeList, index:0i)

                            treeTableSupport.arrowKeyExpansionEnabled = true
                            treeTableSupport.showExpanderForEmptyParent = true

                            noparent {
                                Font localAmountOriginalFont
                                Font globalAmountOriginalFont
                                Font localAmountBoldFont
                                Font globalAmountBoldFont

                                def localDurationClosure = { row->
                                    def task = taskTreeTable.model.getElementAt(row)
                                    boolean progressing = model.workingLog?.taskId == task.id

                                    if (progressing) {
                                        return System.currentTimeMillis() - model.workingLog.start.time
                                    }
                                    null
                                }
                                def globalDurationClosure = { row->
                                    def task = taskTreeTable.model.getElementAt(row)
                                    boolean progressing = model.workingPathIds.any {task.id==it};

                                    if (progressing) {
                                        return System.currentTimeMillis() - model.workingLog.start.time
                                    }
                                    0l
                                }

                                def localAmountFontClosure = {JLabel res, row->

                                    def task = taskTreeTable.model.getElementAt(row)
                                    boolean progressing = model.workingLog?.taskId == task.id


                                    if (progressing) {
                                        if (null==localAmountBoldFont) {
                                            localAmountBoldFont = res.getFont ().deriveFont (Font.BOLD)
                                        }

                                        res.setFont (localAmountBoldFont)
                                    } else {
                                        if (null==localAmountOriginalFont) {
                                            localAmountOriginalFont = res.getFont ().deriveFont (Font.PLAIN)
                                        }
                                        res.setFont (localAmountOriginalFont)
                                    }
                                }

                                def globalAmountFontClosure = {JLabel res, row->

                                    def task = taskTreeTable.model.getElementAt(row)
                                    boolean progressing = model.workingPathIds.any {task.id==it};

                                    if (progressing) {
                                        if (null==globalAmountBoldFont) {
                                            globalAmountBoldFont = res.getFont ().deriveFont (Font.BOLD)
                                        }

                                        res.setFont (globalAmountBoldFont)
                                    } else {
                                        if (null==globalAmountOriginalFont) {
                                            globalAmountOriginalFont = res.getFont ().deriveFont (Font.PLAIN)
                                        }
                                        res.setFont (globalAmountOriginalFont)
                                    }
                                }

                                taskTreeTable.columnModel.getColumn(1i).setCellRenderer(new DurationTableCellRenderer (durationClosure:localDurationClosure, fontClosure: localAmountFontClosure))
                                taskTreeTable.columnModel.getColumn(2i).setCellRenderer(new DurationTableCellRenderer (durationClosure:globalDurationClosure, fontClosure: globalAmountFontClosure))

                                taskTreeTable.selectionModel.addListSelectionListener( [valueChanged: {ListSelectionEvent evt ->
                                    if ( !evt.isAdjusting) {

                                        def selectionIndex = evt.source.leadSelectionIndex


                                        if (selectionIndex!=null && selectionIndex>=0) {
                                            def task = taskTreeTable.model.getElementAt (selectionIndex)
                                            //controller.selectedTaskChanged (task.id)
                                            model.selectedTaskId = task.id
                                            model.selectedTaskIndex = selectionIndex
                                            //... do stuff with the selected index ...
                                        } else {
                                            model.selectedTaskId = null
                                            //controller.selectedTaskChanged (null)
                                        }
                                    }
                                }] as ListSelectionListener)

                                model.taskTreeList.addListEventListener ({
                                    edt{
                                        if (model.selectedTaskIndex) {
                                            taskTreeTable.selectionModel.setSelectionInterval(model.selectedTaskIndex, model.selectedTaskIndex)
                                        }
                                    }
                                } as ListEventListener)
                            }
//                            noparent {
//                                taskTreeTable.columnModel.getColumn(1i).setCellRenderer(
//                                    cellRenderer {
//                                        label(foreground: java.awt.Color.BLACK)
//                                        onRender { children[0].text = value }
//                                    }
//                                )
//                            }
                        }
                    }

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
    dockingFrame(preferredSize: new java.awt.Dimension(400,600)){
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


                    noparent {
                        Font originalFont
                        Font boldFont

                        worklogTable.columnModel.getColumn(1i).setCellRenderer(new DurationTableCellRenderer (durationClosure: {row->
                            def worklog = worklogTable.model.getElementAt(row)
                            boolean progressing = worklog.amount==null;

                            if (progressing) {
                                return System.currentTimeMillis() - worklog.start.time
                            }
                            0l
                        }, fontClosure: {JLabel res, row->

                            def worklog = worklogTable.model.getElementAt(row)
                            boolean progressing = worklog.amount==null;

                            if (null==originalFont) {
                                originalFont = res.getFont ();
                            }
                            if (progressing) {
                                if (null==boldFont) {
                                    boldFont = originalFont.deriveFont (Font.BOLD);
                                }
                                res.setFont (boldFont);
                            } else {
                                res.setFont (originalFont);
                            }


                        }
                        ))
                    }

                }
            }
        }
    }
}
