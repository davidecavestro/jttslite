package jttslite

import groovy.sql.Sql

/**
 * Business logic for Workspace entities.
 *
 * @author Davide Cavestro
 */
class WorkspaceService {
    public int doInsert(def name, def description=null) {
        def newId
        withSql { String dataSourceName, Sql sql ->
            def keys = sql.executeInsert('INSERT INTO workspace (name, description) VALUES (?,?)',
                    [name, description])
            newId = keys[0][0]
        }
        return newId
    }
    public void doUpdate(def id, def name, def description) {
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE workspace SET name=?, description=? WHERE id=?',
                    [name, description, id]) != null
        }
    }
    public int doDelete(def id) {
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('DELETE FROM workspace WHERE id=?',
                    [id])
        }
    }
    def getWorkspace(def id) {
        withSql { String dataSourceName, Sql sql ->
            def result=[]
            sql.eachRow('SELECT * FROM workspace WHERE id=?',
                    [id], {
                        result<<[id:it.id, name:it.name, description:it.description]
                    }
            )
            return result?result[0]:null
        }
    }
    def getWorkspaces() {
        withSql { String dataSourceName, Sql sql ->
            def result=[]
            sql.eachRow('SELECT * FROM workspace', {
                    result<<[id:it.id, name:it.name, description:it.description]
                }
            )
            return result
        }
    }
}