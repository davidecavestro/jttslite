package jttslite

import groovy.sql.Sql
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching

class WorklogService {
    @CacheEvict(value="worklogs", allEntries=true)
    def doInsert(def taskId, def start=null, def amount=null, def comment=null) {
        def newId
        withSql { String dataSourceName, Sql sql ->
            def keys = sql.executeInsert('INSERT INTO worklog (taskId, start, amount, comment) VALUES (?,?,?,?)',
                    [taskId, start?start:new Date (), amount, comment])
            newId = keys[0][0]
        }
        return newId
    }
    @CacheEvict(value="worklogs", allEntries=true)
    def doStart(def taskId, def comment=null) {
        doInsert (taskId, null, null, comment)
    }
    @CacheEvict(value="worklogs", allEntries=true)
    def doStop(def worklogId) {
        withSql { String dataSourceName, Sql sql ->
            def start = sql.firstRow('SELECT start FROM worklog WHERE id=?',[worklogId]).start
            def amount = System.currentTimeMillis()-start.time
            sql.executeUpdate('UPDATE worklog SET amount=? WHERE id= ?',
                    [amount, worklogId])
        }
    }

    @CacheEvict(value="worklogs", allEntries=true)
    public int doDelete(def ids) {
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE worklog SET deleted=TRUE WHERE id IN (?)',
                    [ids.join (',')])
        }
    }

    @Cacheable("worklogs")
    def getWorklog(def worklogId) {
        withSql { String dataSourceName, Sql sql ->
            sql.firstRow('SELECT * FROM worklog WHERE id=? AND deleted=FALSE',[worklogId])
        }
    }

    @Cacheable("worklogs")
    def getWorklogs(def taskId) {
        withSql { String dataSourceName, Sql sql ->
            def result=[]
            sql.eachRow('SELECT * FROM worklog WHERE taskId=? AND deleted<>TRUE',
                    [taskId], {result<<[id:it.id, taskId:it.taskId, start:it.start, amount:it.amount, comment:it.comment]})
            return result
        }
    }

    @Cacheable("worklogs")
    def getWorkingLog(def workspaceId) {
        withSql { String dataSourceName, Sql sql ->
            sql.firstRow('SELECT worklog.id AS id, worklog.taskId AS taskId, worklog.start AS start, worklog.amount AS amount, worklog.comment AS comment FROM worklog, task WHERE worklog.amount IS NULL AND worklog.taskId=task.id AND task.workspaceId=?',[workspaceId])
        }
    }
}