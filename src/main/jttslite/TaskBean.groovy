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

import groovy.beans.Bindable

/**
 * Represents a <em>task table</em> record data.
 *
 * @author Davide Cavestro
 */
@Bindable
class TaskBean {
    /**
     * The record id
     */
    Long id
    /**
     * The workspace id
     */
    Long workspaceId
    /**
     * The parent task id
     */
    Long parentId
    /**
     * The index of this task within its parent's children list
     */
    Integer siblingIndex
    /**
     * A unique value (within the same workspace) reflecting the task tree depth and sibling index.
     * <p>
     * It can be used to easily identify the tasks belonging to a certain subtree.
     * </p>
     */
    String treeCode
    /**
     * The task tree level of this task (root is 0, its children are at 1 and so on)
     */
    Long treeDepth
    /**
     * A title
     */
    String title
    /**
     * An optional description
     */
    String description
    /**
     * The sum of direct worklogs amounts
     */
    Long localAmount
    /**
     * The sum of worklogs amounts for the entire task subtree
     */
    Long globalAmount
    /**
     * The sum of localAmount plus working log amount
     */
    Long localWorkingAmount
    /**
     * The sum of globalAmount plus working log amount
     */
    Long globalWorkingAmount
}
