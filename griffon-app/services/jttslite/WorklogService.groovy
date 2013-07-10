package jttslite

import groovy.sql.Sql
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching

class WorklogService {
    @CacheEvict(value="worklogs", allEntries=true)
    public long doInsert(long taskId, Date start=null, Long amount=null, String comment=null) {
        def newId
        withSql { String dataSourceName, Sql sql ->
            def keys = sql.executeInsert('INSERT INTO worklog (taskId, start, amount, comment) VALUES (?,?,?,?)',
                    [taskId, start?start:new Date (), amount, comment])
            newId = keys[0][0]
        }
        return newId
    }
    @CacheEvict(value="worklogs", allEntries=true)
    public long doStart(long taskId, String comment=null) {
        doInsert (taskId, null, null, comment)
    }
    @CacheEvict(value="worklogs", allEntries=true)
    public void doStop(long worklogId) {
        withSql { String dataSourceName, Sql sql ->
            def start = sql.firstRow('SELECT start FROM worklog WHERE id=?',[worklogId]).start
            def amount = System.currentTimeMillis()-start.time
            sql.executeUpdate('UPDATE worklog SET amount=? WHERE id= ?',
                    [amount, worklogId])
        }
    }

    @CacheEvict(value="worklogs", allEntries=true)
    public int doDelete(Collection<Long> ids) {
        if (!ids) {
            return
        }
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE worklog SET deleted=TRUE WHERE id IN (?)',
                    [ids.join (',')])
        }
    }

    @Cacheable("worklogs")
    WorklogBean getWorklog(long worklogId) {
        withSql { String dataSourceName, Sql sql ->
            toBean (sql.firstRow('SELECT * FROM worklog_period WHERE id=? AND deleted=FALSE',[worklogId]))
        }
    }

    @Cacheable("worklogs")
    List<WorklogBean> getWorklogs(long taskId) {
        withSql { String dataSourceName, Sql sql ->
            return sql.rows('SELECT * FROM worklog_period WHERE taskId=? AND deleted<>TRUE',
                    [taskId]).collect {toBean (it)}
        }
    }

    @Cacheable("worklogs")
    WorklogBean getWorkingLog(long workspaceId) {
        withSql { String dataSourceName, Sql sql ->
            toBean (sql.firstRow('SELECT * FROM worklog_period, task WHERE worklog_period.amount IS NULL AND worklog_period.taskId=task.id AND task.workspaceId=?',[workspaceId]))
        }
    }

    /**
     * Returns a WorklogBean instance initialized with the specified properties
     * @param props property values for the new bean
     */
    private WorklogBean toBean (Map props) {
        if (props==null) {
            return null
        }
        return new WorklogBean(
            id: props.id,
            amount: props.amount,
            comment: props.comment,
            start: props.start,
            taskId: props.taskId
        )
    }

}