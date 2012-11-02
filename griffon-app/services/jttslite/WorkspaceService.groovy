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
}