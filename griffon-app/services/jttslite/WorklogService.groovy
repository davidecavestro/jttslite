package jttslite

import groovy.sql.Sql

class WorklogService {
    def doInsert(def taskId, def start=null, def amount=null, def comment=null) {
        def newId
        withSql { String dataSourceName, Sql sql ->
            def keys = sql.executeInsert('INSERT INTO worklog (taskId, start, amount, comment) VALUES (?,?,?,?)',
                    [taskId, start?start:System.currentTimeMillis(), amount, comment])
            newId = keys[0][0]
        }
        return newId
    }
    def doStart(def taskId, def comment=null) {
        doInsert (taskId, null, null, comment)
    }
    def doStop(def worklogId) {
        withSql { String dataSourceName, Sql sql ->
            def start = sql.firstRow('SELECT start FROM worklog WHERE id=?',[worklogId]).start
            def amount = System.currentTimeMillis()-start
            sql.executeUpdate('UPDATE worklog SET amount=? WHERE id= ?',
                    [amount, worklogId])
        }
    }
    def getWorklog(def worklogId) {
        withSql { String dataSourceName, Sql sql ->
            sql.firstRow('SELECT * FROM worklog WHERE id=?',[worklogId])
        }
    }
    def getWorklogs(def taskId) {
        withSql { String dataSourceName, Sql sql ->
            def result=[]
            sql.eachRow('SELECT * FROM worklog WHERE taskId=?',
                    [taskId], {result<<[id:it.id, taskId:it.taskId, start:it.start, amount:it.amount, comment:it.comment]})
        }
    }
}