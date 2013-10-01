databaseChangeLog() {

    changeSet(id:'initial-schema', author: 'davidecavestro') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            """
DROP TABLE IF EXISTS dictionary;
CREATE TABLE dictionary (
    key VARCHAR (1000) NOT NULL PRIMARY KEY,
    stringValue VARCHAR (10000),
    longValue LONG,
    booleanValue BOOLEAN,
    ts TIMESTAMP AS CURRENT_TIMESTAMP()
);

DROP TABLE IF EXISTS workspace;
CREATE TABLE workspace (
    id LONG NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR (4000),
    description VARCHAR (10000),
    deleted BOOLEAN DEFAULT FALSE,
    ts TIMESTAMP AS CURRENT_TIMESTAMP()
);

DROP TABLE IF EXISTS task CASCADE;
CREATE TABLE task (
    id LONG NOT NULL AUTO_INCREMENT PRIMARY KEY,
    workspaceId LONG NOT NULL,
    parentId LONG,
    siblingIndex INTEGER NOT NULL,
    treeCode VARCHAR(1000) NOT NULL,
    treeDepth LONG NOT NULL,
    title VARCHAR(4000) NOT NULL,
    description VARCHAR(10000),
    deleted BOOLEAN DEFAULT FALSE,
    ts TIMESTAMP AS CURRENT_TIMESTAMP(),

    CONSTRAINT fkTaskWorkspace FOREIGN KEY (workspaceId) REFERENCES workspace ON DELETE CASCADE

);

DROP TABLE IF EXISTS worklog CASCADE;
CREATE TABLE worklog (
    id LONG NOT NULL AUTO_INCREMENT PRIMARY KEY,
    taskId LONG NOT NULL,
    start DATETIME NOT NULL, // start time
    amount LONG, //duration in millis
    comment VARCHAR(10000),
    deleted BOOLEAN DEFAULT FALSE,
    ts TIMESTAMP AS CURRENT_TIMESTAMP(),

    CONSTRAINT fkWorklogTask FOREIGN KEY (taskId) REFERENCES task ON DELETE CASCADE

);

DROP VIEW IF EXISTS task_worklogs CASCADE;
CREATE VIEW task_worklogs AS
(
SELECT
    tk.id,
    IFNULL (SUM (wl.amount),0) AS localAmount,
    IFNULL (MIN (
        SELECT
            SUM (wl1.amount)
        FROM
            worklog wl1 INNER JOIN task tk1 ON (wl1.taskId=tk1.id) WHERE tk1.treeCode LIKE CONCAT (tk.treeCode, '%')),0) AS globalAmount
FROM
    worklog wl RIGHT OUTER JOIN task tk ON (wl.taskid=tk.id) WHERE wl.deleted<>TRUE AND tk.deleted<>TRUE
GROUP BY
    tk.id
);


DROP VIEW IF EXISTS worklog_period CASCADE;
CREATE VIEW worklog_period AS
(
SELECT
    id, taskid, start, DATEADD ('MS', amount, start) AS finish, amount, comment
FROM worklog WHERE deleted<>TRUE
);

"""
        }
    }
}