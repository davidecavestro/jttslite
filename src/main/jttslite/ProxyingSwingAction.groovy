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

import griffon.swing.SwingAction

import javax.swing.*
import java.awt.event.ActionEvent
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

/**
 * Wraps dynamically set actions in order to reflect the behavior of the current one.
 *
 * @author http://griffon-user.3225736.n2.nabble.com/Any-pattern-to-implement-cross-widget-focus-related-actions-delete-copy-cut-paste-td7578331.html#a7578343
 * @author Davide Cavestro
 *
 */
public class ProxyingSwingAction extends AbstractAction {

    private SwingAction delegateAction
    /**
     * Propagates target action changes to this one
     */
    private final PropertyChangeListener propertyUpdater = new PropertyChangeListener() {
        @Override
        void propertyChange(PropertyChangeEvent event) {
            //apply this[event.propertyName] <= event.newValue
            println "propertyChange $event"
            ProxyingSwingAction.this.putValue (event.propertyName, event.newValue)
        }
    }


    void actionPerformed(ActionEvent event) {
        delegateAction.actionPerformed(event)
    }

//    void wrap(Action delegateAction) {
    void wrap(griffon.core.controller.GriffonControllerAction delegateControllerAction) {
        SwingAction newDelegateAction = delegateControllerAction?.toolkitAction
        if (delegateAction != newDelegateAction) {
            if (delegateAction) {
                delegateAction.removePropertyChangeListener(propertyUpdater)
            }
            if (newDelegateAction) {
                delegateAction = newDelegateAction
                delegateAction.addPropertyChangeListener(propertyUpdater)
                // copy all delegateAction properties to this
                copyProperties(delegateAction, this)
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


    def copyProperties(AbstractAction source, AbstractAction target){
        /*
        target.metaClass.properties.each{
            if (source.metaClass.hasProperty(source, it.name)
                    && it.name != 'metaClass'
                    && it.name != 'class'
                    && it.name != 'keys'
                    && it.name != 'propertyChangeListeners'
                ) {
                def value = source.metaClass.getProperty(source, it.name)
                println "copying ${it.name} property to $target with value $value"
                it.setProperty(target, value)
            }
        } */
        //remove old properties
        target.keys?.each {
            target.putValue (it, null)
        }

        target.setEnabled (source.enabled)
        //copy new ones from source
        source.keys?.each {
            if (it instanceof String && it!="enabled") {
                def theValue = source.getValue (it)
                target.putValue (it, theValue)
            }
        }
    }
}

