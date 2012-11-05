package jttslite

import groovy.sql.Sql

class TaskService {
    def doInsert(def workspaceId, def parentId, def title, def description) {
        def newId
        withSql { String dataSourceName, Sql sql ->
            def keys = sql.executeInsert('INSERT INTO task (workspaceId, parentId, title, description) VALUES (?,?,?,?)',
                    [workspaceId, parentId, title, description]) != null
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
            sql.executeUpdate('UPDATE task SET parentId=?, siblingIndex=?, treeCode=?+'.'+? WHERE id=?',
                    [newParent.id, siblingIndex, newParent.treeCode, siblingIndex, id]) != null
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
                        result<<[id:it.id, workspaceId:it.workspaceId, parentId:it.parentId, siblingIndex:it.siblingIndex, treeCode:it.treeCode, title:it.title, description:it.description]
                    }
            )
        }
    }
}