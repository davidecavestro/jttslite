/*
 * Copyright (c) 2013, the original author or authors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package jttslite

import ca.odell.glazedlists.EventList
import ca.odell.glazedlists.swing.TreeTableSupport
import net.miginfocom.swing.MigLayout
import org.jdesktop.swingx.JXTable
import org.jfree.chart.ChartPanel

import javax.swing.*
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import javax.swing.table.JTableHeader
import java.awt.*
import java.awt.event.FocusEvent
import java.awt.event.FocusListener


GriffonApplication application = app

splitPane {
    splitPane(id:'mainPanel',orientation:JSplitPane.VERTICAL_SPLIT){
        dockingFrame(preferredSize: new java.awt.Dimension(300,400)) {
            panel(title:'Tasks'){
                borderLayout()
                busyComponent(id: "c0", constraints: CENTER/*, busy: bind(source:model,sourceProperty:'searchResultsPanelEnabled')*/) {
                    busyModel(description: "Please Wait...")
                    scrollPane(name:'taskTreeTableWrapper',constraints:BorderLayout.CENTER) {
                        noparent {
                            selectionModel = new ca.odell.glazedlists.swing.EventSelectionModel(model.taskTreeList)//see https://sites.google.com/site/glazedlists/documentation/faq#TOC-JLists-JTables
                            selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
                        }
                        JXTable taskTreeTable = new JXTable() {
                            Component prepareRenderer() {
                                super.prepareRenderer ()
                            }
                        }

                        taskTreeTable.setSortable(false);
                        taskTreeTable.getTableHeader().setDefaultRenderer(new JTableHeader().getDefaultRenderer());
                        taskTreeTable.setAutoCreateRowSorter(false);
                        taskTreeTable.setRowSorter(null);
                        taskTreeTable.columnControlVisible = true
                        taskTreeTable.fillsViewportHeight = false //see http://glazedlists.1045722.n5.nabble.com/JXTable-viewport-not-repainted-after-clearing-the-eventList-tp5709890p5709929.html

                        jxtable(taskTreeTable, id:'taskTree', selectionModel:selectionModel) {
                            tableFormat = defaultWritableTableFormat(columns: [
                                    [name: 'title', title: 'Name', write: {target, columnNames, index, editedValue->
                                        //maintain selection in case of leaf nodes, see http://glazedlists.1045722.n5.nabble.com/TreeList-fires-insert-delete-event-on-update-JTable-selection-lost-td3418617.html
                                        controller.renameTask (target.id, editedValue)
                                    }],
                                    [name: 'localWorkingAmount', title: 'Local amount', read: {target, columnNames, index->
                                        def localWorkingAmount = target.localWorkingAmount
                                        println "localWorkingAmount is $localWorkingAmount"
                                        //uses working (bound) amount if available, otherwise use persistent data
                                        localWorkingAmount!=null?localWorkingAmount:target.localAmount
                                    }],
                                    [name: 'globalWorkingAmount', title: 'Subtree amount', read: {target, columnNames, index->
                                        def globalWorkingAmount = target.globalWorkingAmount
                                        println "globalWorkingAmount is $globalWorkingAmount"
                                        globalWorkingAmount!=null?globalWorkingAmount:target.globalAmount
                                    }]],
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

                                /*
                                 * working log amount processing
                                 */
                                def localDurationClosure = { row->
                                    TaskBean task = taskTreeTable.model.getElementAt(row)
                                    boolean progressing = model.workingLog?.taskId == task.id

                                    if (progressing) {
                                        return System.currentTimeMillis() - model.workingLog.start.time
                                    }
                                    null
                                }
                                def globalDurationClosure = { row->
                                    TaskBean task = taskTreeTable.model.getElementAt(row)
                                    boolean progressing = model.workingPathIds.any {task.id==it};

                                    if (progressing) {
                                        return System.currentTimeMillis() - model.workingLog.start.time
                                    }
                                    0l
                                }

                                def localAmountFontClosure = {JLabel res, row->

                                    TaskBean task = taskTreeTable.model.getElementAt(row)
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

                                    TaskBean task = taskTreeTable.model.getElementAt(row)
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

                                taskTreeTable.columnModel.getColumn(1i).setCellRenderer(new DurationTableCellRenderer (/*durationClosure:localDurationClosure, */fontClosure: localAmountFontClosure))
                                taskTreeTable.columnModel.getColumn(2i).setCellRenderer(new DurationTableCellRenderer (/*durationClosure:globalDurationClosure, */fontClosure: globalAmountFontClosure))

                                taskTreeTable.selectionModel.addListSelectionListener( [valueChanged: {ListSelectionEvent evt ->
                                    if ( !evt.valueIsAdjusting) {

                                        int selectionIndex = evt.firstIndex
                                        int lastIndex = evt.lastIndex


                                        def leadSelectionIndex = taskTreeTable.selectionModel.getLeadSelectionIndex()
                                        def lastSelectionIndex = taskTreeTable.selectionModel.getAnchorSelectionIndex()
                                        //if (selectionIndex!=null && selectionIndex>=0) {
                                        def selectedElement = taskTreeTable.model.getElementAt (selectionIndex)

                                        EventList selected = taskTreeTable.selectionModel.getSelected()
                                        model.taskSelection = selected

                                        def selectedTaskId = null
                                        if (!selected.isEmpty()) {
                                            def task = selected.get(0)
                                            selectedTaskId = task.id
                                        } else {
                                            selectedTaskId = null
                                        }
                                        model.selectedTaskId = selectedTaskId
                                    }
                                }] as ListSelectionListener)
                                taskTreeTable.addFocusListener(new FocusListener() {
                                    @Override
                                    void focusGained(FocusEvent e) {
                                        //synch focus-related actions
                                        application.event('TreeGainedFocus')
                                    }

                                    @Override
                                    void focusLost(FocusEvent e) {
                                        application.event('TreeLostFocus')
                                    }
                                })
                            }
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
                    JXTable worklogTable = new JXTable() {
                        Component prepareRenderer() {
                            super.prepareRenderer ()
                        }
                    }

                    worklogTable.setSortable(false);
                    worklogTable.getTableHeader().setDefaultRenderer(new JTableHeader().getDefaultRenderer());
                    worklogTable.setAutoCreateRowSorter(false);
                    worklogTable.setRowSorter(null);
                    worklogTable.columnControlVisible = true
                    worklogTable.fillsViewportHeight = false //see http://glazedlists.1045722.n5.nabble.com/JXTable-viewport-not-repainted-after-clearing-the-eventList-tp5709890p5709929.html

                    jxtable(worklogTable, id:'worklogTable', selectionModel:selectionModel) {
                        tableFormat = defaultAdvancedTableFormat(columns: [
                                [name: 'start',     title: 'Start'],
                                [name: 'amount',     title: 'Duration'],
                                [name: 'comment', title: 'Comment']])
                        eventTableModel(source:model.swingProxyWorklogList, format:tableFormat)

                        current.selectionModel.addListSelectionListener( [valueChanged: {ListSelectionEvent evt ->
                            if ( !evt.valueIsAdjusting) {

                                EventList selected = worklogTable.selectionModel.getSelected()
                                model.worklogSelection = selected
                            }
                        }] as ListSelectionListener)

                    }


                    noparent {
                        Font originalFont
                        Font boldFont

                        worklogTable.columnModel.getColumn(1i).setCellRenderer(new DurationTableCellRenderer (durationClosure: {row->
                            WorklogBean worklog = worklogTable.model.getElementAt(row)
                            boolean progressing = worklog.amount==null;

                            if (progressing) {
                                return System.currentTimeMillis() - worklog.start.time
                            }
                            0l
                        }, fontClosure: {JLabel res, row->

                            WorklogBean worklog = worklogTable.model.getElementAt(row)
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
                    worklogTable.addFocusListener(new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {
                            //synch focus-related actions
                            application.event('TableGainedFocus')
                        }

                        @Override
                        void focusLost(FocusEvent e) {
                            application.event('TableLostFocus')
                        }
                    })

                }
            }
        }
    }
}
