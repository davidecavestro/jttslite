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
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.util.LinkedCaseInsensitiveMap

/**
 * Business logic for {@link TaskBean tasks}.
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
    @Caching(evict=[
        @CacheEvict(value="tasks", allEntries=true),
        @CacheEvict(value="workspaceTasks", allEntries=true)
    ])
    public long doInsert(long workspaceId, Long parentId, String title=null, String description=null) {
        if (workspaceId==null) {
            withSql { String dataSourceName, Sql sql ->
                workspaceId = sql.firstRow('SELECT * FROM task WHERE id=?', [parentId]).workspaceId
            }
        }
        def newId
        withSql { String dataSourceName, Sql sql ->
            def siblingIndex
            def treeDepth = 0
            def treeCode
            def parent
            if (parentId!=null) {
                siblingIndex = sql.firstRow('SELECT MAX(siblingIndex) AS siblingIndex FROM task WHERE parentId=?', [parentId]).siblingIndex
                parent = getTask (parentId)
                if (parent) {
                    treeDepth = parent.treeDepth+1
                }
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

    @Caching(evict=[
    @CacheEvict(value="tasks", allEntries=true),
    @CacheEvict(value="workspaceTasks", allEntries=true)
    ])
    public void doUpdate(long id, String title, String description) {
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE task SET title=?, description=? WHERE id=?',
                    [title, description, id]) != null
        }
    }

    @Caching(evict=[
    @CacheEvict(value="tasks", allEntries=true),
    @CacheEvict(value="workspaceTasks", allEntries=true)
    ])
    public void doRename(long id, String title) {
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE task SET title=? WHERE id=?',
                    [title, id]) != null
        }
    }

    @Caching(evict=[
    @CacheEvict(value="tasks", allEntries=true),
    @CacheEvict(value="workspaceTasks", allEntries=true)
    ])
    public void doMove(long id, String parentId, int siblingIndex) {
        def task = getTask (id)
        assert task.parentId!=null, "Cannot move workspace root"

        def oldParent = getTask(task.parentId)

        def newParent = getTask(parentId)
        //shift right new siblings
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE task SET siblingIndex=siblingIndex+1, treeCode=?+'.'+(siblingIndex+1) WHERE parentId=? AND siblingIndex>=? AND deleted<>TRUE',
                    [newParent.treeCode, parentId, siblingIndex]) != null
        }
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE task SET parentId=?, siblingIndex=?, treeDepth=?, treeCode=?+'.'+? WHERE id=?',
                    [newParent.id, siblingIndex, newParent.treeDepth+1, newParent.treeCode, siblingIndex, id]) != null
        }
        //shift left old siblings
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE task SET siblingIndex=siblingIndex-1, treeCode=?+'.'+(siblingIndex-1) WHERE parentId=? AND siblingIndex>? AND deleted<>TRUE',
                    [oldParent.treeCode, oldParent.id, task.siblingIndex]) != null
        }
    }

    @Caching(evict=[
    @CacheEvict(value="tasks", allEntries=true),
    @CacheEvict(value="workspaceTasks", allEntries=true)
    ])
    public int doDelete(Collection<Long> ids) {
        if (!ids) {
            return
        }
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE task SET deleted=TRUE WHERE id IN (?)',
                    [ids.join (',')])
        }
    }
    @Cacheable("tasks")
    public TaskBean getTask(Long id) {
        withSql { String dataSourceName, Sql sql ->
            toBean (sql.firstRow('SELECT * FROM task t INNER JOIN task_worklogs tw ON (t.id=tw.id) WHERE t.id=?', [id]))
        }
    }
    @Cacheable("workspaceTasks")
    List<TaskBean> getTasks(long workspaceId) {
        withSql { String dataSourceName, Sql sql ->
            sql.rows('SELECT * FROM task t INNER JOIN task_worklogs tw ON (t.id=tw.id) WHERE t.workspaceId=? AND deleted<>TRUE',[workspaceId]).collect {toBean (it)}
        }
    }

    @Cacheable("taskPathIds")
    public List<Long> getTaskPathIds(Long taskId) {
        println "getTaskPathIds called for taskId: $taskId (${taskId.getClass ()})"
        withSql { String dataSourceName, Sql sql ->
            def result=[]
            sql.eachRow("""
             WITH link(id, parentId, treeCode) AS (
                SELECT id, parentId, treeCode
                FROM task
                WHERE task.id=${taskId}
                UNION ALL
                SELECT task.id, task.parentId, task.treeCode
                FROM link INNER JOIN task ON link.parentid = task.id
            ) SELECT id FROM link ORDER BY treeCode ASC;
            """,
                [/*taskId*/], {
                    result<< it.id
                }
            )
            result
        }
    }

    /**
     * Returns a TaskBean instance initialized with the specified properties
     * @param props property values for the new bean
     */
    private TaskBean toBean (Map props) {
        if (props==null) {
            return null
        }
        return new TaskBean(
            id: props.id,
            workspaceId: props.workspaceId,
            treeDepth: props.treeDepth,
            treeCode: props.treeCode,
            title: props.title,
            description: props.description,
            parentId: props.parentId,
            siblingIndex: props.siblingIndex,
            globalAmount: props.globalAmount,
            localAmount: props.localAmount
        )
    }
}