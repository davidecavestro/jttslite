package jttslite

import groovy.sql.Sql

class TaskService {
    def doInsert(def workspaceId, def parentId, def siblingIndex, def treeCode, title, description) {
        withSql { String dataSourceName, Sql sql ->
            sql.executeInsert('INSERT INTO task (workspaceId, parentId, siblingIndex, treeCode, title, description) VALUES (?,?,?,?,?,?)',
                    []) != null
        }
    }
    def doStart(String comment=null) {
        withSql { String dataSourceName, Sql sql ->
            sql.executeInsert('INSERT INTO task (workspaceId, parentId, siblingIndex, treeCode, title, description) VALUES (?,?,?,?,?,?)',
                    []) != null
        }
    }
}