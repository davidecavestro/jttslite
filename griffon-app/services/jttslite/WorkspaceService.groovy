package jttslite

import groovy.sql.Sql

/**
 * Business logic for Workspace entities.
 *
 * @author Davide Cavestro
 */
class WorkspaceService {
    public long doInsert(String name, String description=null) {
        def newId
        withSql { String dataSourceName, Sql sql ->
            def keys = sql.executeInsert('INSERT INTO workspace (name, description) VALUES (?,?)',
                    [name, description])
            newId = keys[0][0]
        }
        return newId
    }
    public int doUpdate(long id, String name, String description) {
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE workspace SET name=?, description=? WHERE id=?',
                    [name, description, id]) != null
        }
    }
    public int doDelete(Long id) {
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('UPDATE workspace SET deleted=TRUE WHERE id=?',
                    [id])
        }
    }
    public WorkspaceBean getWorkspace(Long id) {
        withSql { String dataSourceName, Sql sql ->
            toBean (sql.firstRow("""\
SELECT
    id AS id,
    description AS description,
    name AS name
FROM workspace
WHERE id=?""", [id]))
        }
    }
    public List<WorkspaceBean> getWorkspaces() {
        withSql { String dataSourceName, Sql sql ->
            sql.rows("""\
SELECT
    id AS id,
    description AS description,
    name AS name
FROM workspace
WHERE deleted=FALSE""").collect {toBean (it)}
        }
    }

    public boolean hasWorkspaces() {
        return workspacesCount()>0
    }

    public int workspacesCount() {
        withSql { String dataSourceName, Sql sql ->
            def result
            result = sql.firstRow('SELECT COUNT(*) AS wsnum FROM workspace WHERE deleted<>TRUE').wsnum
            return result
        }
    }

    /**
     * Returns a WorkspaceBean instance initialized with the specified properties
     * @param props property values for the new bean
     */
    private WorkspaceBean toBean (Map props) {
        if (props==null) {
            return null
        }
        return new WorkspaceBean(
            id: props.id,
            description: props.description,
            name: props.name
        )
    }
}