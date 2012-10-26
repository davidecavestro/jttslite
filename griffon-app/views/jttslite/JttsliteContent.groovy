package jttslite


import javax.swing.JSplitPane
import java.awt.BorderLayout
import net.miginfocom.swing.MigLayout

import org.viewaframework.widget.swing.table.*
import org.viewaframework.swing.*

import java.awt.Color
import java.awt.Font
import org.jfree.chart.labels.PieToolTipGenerator
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.data.general.DefaultPieDataset
import groovy.swing.SwingBuilder
import java.awt.*
import javax.swing.WindowConstants as WC
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
                    jxtree( id: "tasks" )
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
