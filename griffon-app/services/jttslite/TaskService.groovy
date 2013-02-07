/*
 * Copyright (c) 2012, the original author or authors.
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

import groovy.sql.Sql

/**
 * Business logic for tasks.
 * <p>
 * A task is a model object.
 * </p>
 *
 * @author Davide Cavestro
 */
class TaskService {
    /**
     * Insert a new task.
     *
     * @param workspaceId the workspace
     * @param parentId the task parent id
     * @param title the task title
     * @param description the task description
     * @return the new task id
     */
    def doInsert(def workspaceId, def parentId, def title=null, def description=null) {
        if (workspaceId==null) {
            withSql { String dataSourceName, Sql sql ->
                workspaceId = sql.firstRow('SELECT * FROM task WHERE id=?', [parentId]).workspaceId
            }
        }
        def newId
        withSql { String dataSourceName, Sql sql ->
            def siblingIndex
            def treeDepth
            def treeCode
            def parent
            if (parentId!=null) {
                siblingIndex = sql.firstRow('SELECT MAX(siblingIndex) AS siblingIndex FROM task WHERE parentId=?', [parentId]).siblingIndex
                parent = getTask (parentId)
                treeDepth = parent.treeDepth+1
            } else {
                treeDepth = 0
            }
            if (siblingIndex!=null) {
                siblingIndex++
            } else {
                siblingIndex = 0
            }
            if (parent!=null) {
                treeCode = "${parent.treeCode}.${siblingIndex+1}".toString()
            } else {
                treeCode = "${siblingIndex+1}".toString ()
            }

            def keys = sql.executeInsert('INSERT INTO task (workspaceId, parentId, title, description, siblingIndex, treeDepth, treeCode) VALUES (?,?,?,?,?,?,?)',
                    [workspaceId, parentId, title, description, siblingIndex, treeDepth, treeCode])
            newId = keys[0][0]
        }
        return newId
    }
    public void doUpdate(def id, def title, def description) {
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE task SET title=?, description=? WHERE id=?',
                    [title, description, id]) != null
        }
    }

    public void doRename(def id, def title) {
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE task SET title=? WHERE id=?',
                    [title, id]) != null
        }
    }

    public void doMove(def id, def parentId, def siblingIndex) {
        def task = getTask (id)
        assert task.parentId!=null, "Cannot move workspace root"

        def oldParent = getTask(task.parentId)

        def newParent = getTask(parentId)
        //shift right new siblings
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE task SET siblingIndex=siblingIndex+1, treeCode=?+'.'+(siblingIndex+1) WHERE parentId=? AND siblingIndex>=?',
                    [newParent.treeCode, parentId, siblingIndex]) != null
        }
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE task SET parentId=?, siblingIndex=?, treeDepth=?, treeCode=?+'.'+? WHERE id=?',
                    [newParent.id, siblingIndex, newParent.treeDepth+1, newParent.treeCode, siblingIndex, id]) != null
        }
        //shift left old siblings
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE task SET siblingIndex=siblingIndex-1, treeCode=?+'.'+(siblingIndex-1) WHERE parentId=? AND siblingIndex>?',
                    [oldParent.treeCode, oldParent.id, task.siblingIndex]) != null
        }
    }

    public int doDelete(def id) {
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('DELETE FROM task WHERE id=?',
                    [id])
        }
    }
    def getTask(def id) {
        withSql { String dataSourceName, Sql sql ->
            sql.firstRow('SELECT * FROM task WHERE id=?', [id])
        }
    }
    def getTasks(def workspaceId) {
        withSql { String dataSourceName, Sql sql ->
            def result=[]
            sql.eachRow('SELECT * FROM task WHERE workspaceId=?',
                    [workspaceId], {
                        result<<[id:it.id, workspaceId:it.workspaceId, parentId:it.parentId, siblingIndex:it.siblingIndex, treeDepth: it.treeDepth, treeCode:it.treeCode, title:it.title, description:it.description]
                    }
            )
            return result
        }
    }

    def getTaskPath(def taskId) {
        withSql { String dataSourceName, Sql sql ->
            def result=[]
            sql.eachRow("""
            WITH link(id, title, parentId, siblingIndex, treeDepth, treeCode, description, workspaceId) AS (
                SELECT id, title, parentId, siblingIndex, treeDepth, treeCode, description, workspaceId
                FROM task
                WHERE task.id=${taskId}
                UNION ALL
                SELECT task.id, task.title, task.parentId, task.siblingIndex, task.treeDepth, task.treeCode, task.description, task.workspaceId
                FROM link INNER JOIN task ON link.parentid = task.id
            ) SELECT id, title, parentId, siblingIndex, treeDepth, treeCode, description, workspaceId FROM link ORDER BY treeCode ASC;
                """,
                [/*taskId*/], {
                    result<<[id:it.id, workspaceId:it.workspaceId, parentId:it.parentId, siblingIndex:it.siblingIndex, treeDepth:it.treeDepth,  treeCode:it.treeCode, title:it.title, description:it.description]
                }
            )
            result
        }
    }
}