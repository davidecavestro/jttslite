package jttslite

import groovy.sql.Sql

class WorklogService {
    def doInsert(def taskId, def start, def amount, def comment) {
        def newId
        withSql { String dataSourceName, Sql sql ->
            def keys = sql.executeInsert('INSERT INTO worklog (taskId, start, amount, comment) VALUES (?,?,?,?)',
                    [taskId, start, amount, comment]) != null
            newId = keys[0][0]
        }
        return newId
    }
    def doStart(def taskId, def comment=null) {
        doInsert (taskId, new Date().getTime(), null, comment)
    }
    def getWorklogs(def taskId) {
        withSql { String dataSourceName, Sql sql ->
            def result=[]
            sql.eachRow('SELECT * FROM worklog WHERE taskId=?',
                    [taskId], {result<<[id:it.id, taskId:it.taskId, start:it.start, amount:it.amount, comment:it.comment]})
        }
    }
}