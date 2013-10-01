package jttslite

import groovy.sql.Sql
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching

class WorklogService {
    @Caching(evict=[
    @CacheEvict(value="taskWorklogs", key='#taskId'),
    @CacheEvict(value="workspaceWorklog", allEntries=true),
    @CacheEvict(value="worklogById", allEntries=true)
    ])
    public long doInsert(long taskId, Date start=null, Long amount=null, String comment=null) {
        def newId
        withSql { String dataSourceName, Sql sql ->
            def keys = sql.executeInsert('INSERT INTO worklog (taskId, start, amount, comment) VALUES (?,?,?,?)',
                    [taskId, start?start:new Date (), amount, comment])
            newId = keys[0][0]
        }
        return newId
    }
    @Caching(evict=[
    @CacheEvict(value="taskWorklogs", key='#taskId'),
    @CacheEvict(value="workspaceWorklog", allEntries=true),
    @CacheEvict(value="worklogById", allEntries=true)
    ])
    public long doStart(long taskId, String comment=null) {
        doInsert (taskId, null, null, comment)
    }
    @Caching(evict=[
    @CacheEvict(value="taskWorklogs", allEntries=true),
    @CacheEvict(value="workspaceWorklog", allEntries=true),
    @CacheEvict(value="worklogById", key='#worklogId')
    ])
    public void doStop(long worklogId) {
        withSql { String dataSourceName, Sql sql ->
            def start = sql.firstRow('SELECT start FROM worklog WHERE id=?',[worklogId]).start
            def amount = System.currentTimeMillis()-start.time
            sql.executeUpdate('UPDATE worklog SET amount=? WHERE id= ?',
                    [amount, worklogId])
        }
    }

    @Caching(evict=[
    @CacheEvict(value="taskWorklogs", allEntries=true),
    @CacheEvict(value="workspaceWorklog", allEntries=true),
    @CacheEvict(value="worklogById", allEntries=true)
    ])
    public int doDelete(Collection<Long> ids) {
        if (!ids) {
            return
        }
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE worklog SET deleted=TRUE WHERE id IN (?)',
                    [ids.join (',')])
        }
    }

    @Cacheable("worklogById")
    WorklogBean getWorklog(long worklogId) {
        withSql { String dataSourceName, Sql sql ->
            toBean (sql.firstRow("""\
SELECT
    wp.id AS id,
    wp.amount AS amount,
    wp.comment AS comment,
    wp.start AS start,
    wp.taskId AS taskId,
    w.deleted AS deleted
FROM worklog_period wp INNER JOIN worklog w ON (wp.id=w.id)
WHERE w.id=? AND deleted<>TRUE""",
                    [worklogId]))
        }
    }

    @Cacheable("taskWorklogs")
    List<WorklogBean> getWorklogs(long taskId) {
        withSql { String dataSourceName, Sql sql ->
            return sql.rows("""\
SELECT
    wp.id AS id,
    wp.amount AS amount,
    wp.comment AS comment,
    wp.start AS start,
    wp.taskId AS taskId,
    w.deleted AS deleted
FROM worklog_period wp INNER JOIN worklog w ON (wp.id=w.id)
WHERE wp.taskId=? AND deleted<>TRUE""",
                    [taskId]).collect {toBean (it)}
        }
    }

    @Cacheable("workspaceWorklog")
    WorklogBean getWorkingLog(long workspaceId) {
        withSql { String dataSourceName, Sql sql ->
            toBean (sql.firstRow("""\
SELECT
    wp.id AS id,
    wp.amount AS amount,
    wp.comment AS comment,
    wp.start AS start,
    wp.taskId AS taskId,
    w.deleted AS deleted
FROM
    worklog_period wp INNER JOIN worklog w ON (wp.id=w.id) INNER JOIN task t ON (w.taskId=t.id)
WHERE
    wp.amount IS NULL AND
    t.workspaceId=?""",
                    [workspaceId]))
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