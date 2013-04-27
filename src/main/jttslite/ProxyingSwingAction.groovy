package jttslite

import javax.swing.*
import java.awt.event.ActionEvent
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

/**
 *
 * @author http://griffon-user.3225736.n2.nabble.com/Any-pattern-to-implement-cross-widget-focus-related-actions-delete-copy-cut-paste-td7578331.html#a7578343
 * @author Davide Cavestro
 *
 */
public class ProxyingSwingAction extends AbstractAction {

    private Action delegateAction
    private final PropertyChangeListener propertyUpdater = new PropertyChangeListener() {
        @Override
        void propertyChange(PropertyChangeEvent event) {
            //apply this[event.propertyName] <= event.newValue
            this[event.propertyName] <= event.newValue
        }
    }


    void actionPerformed(ActionEvent event) {
        delegateAction.actionPerformed(event)
    }

//    void wrap(Action delegateAction) {
    void wrap(griffon.core.controller.GriffonControllerAction delegateControllerAction) {
        Action newDelegateAction = delegateControllerAction?.toolkitAction
        if (delegateAction != newDelegateAction) {
            if (delegateAction) {
                delegateAction.removePropertyChangeListener(propertyUpdater)
            }
            if (newDelegateAction) {
                delegateAction = newDelegateAction
                delegateAction.addPropertyChangeListener(propertyUpdater)
                // copy all delegateAction properties to this
                copyDelegateProps()
            } else {
                setEnabled (false)
            }
        }
    }

    def copyDelegateProps () {
        def propsMap = delegateAction.properties
        propsMap.remove('metaClass')
        propsMap.remove('class')
        println "copying props $propsMap"
        properties.putAll(propsMap)
    }
}

