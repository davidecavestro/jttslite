databaseChangeLog() {
    //include(path: '20130108-initial-schema.groovy', relativeToChangelog: false)
    changeSet(id:'initial-schema', author: 'davidecavestro') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            """
DROP TABLE IF EXISTS dictionary;
CREATE TABLE dictionary (
    key VARCHAR (1000) NOT NULL PRIMARY KEY,
    stringValue VARCHAR (10000),
    longValue LONG,
    booleanValue BOOLEAN
);

DROP TABLE IF EXISTS workspace;
CREATE TABLE workspace (
    id LONG NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR (4000),
    description VARCHAR (10000),
);

DROP TABLE IF EXISTS task;
CREATE TABLE task (
    id LONG NOT NULL AUTO_INCREMENT PRIMARY KEY,
    workspaceId LONG NOT NULL,
    parentId LONG,
    siblingIndex INTEGER NOT NULL,
    treeCode VARCHAR(1000) NOT NULL,
    treeDepth LONG NOT NULL,
    title VARCHAR(4000) NOT NULL,
    description VARCHAR(10000),

    CONSTRAINT fkTaskWorkspace FOREIGN KEY (workspaceId) REFERENCES workspace ON DELETE CASCADE

);

DROP TABLE IF EXISTS worklog;
CREATE TABLE worklog (
    id LONG NOT NULL AUTO_INCREMENT PRIMARY KEY,
    taskId LONG NOT NULL,
    start DATETIME NOT NULL, // start time in millis from epoch
    amount LONG, //duration in millis
    comment VARCHAR(10000),

    CONSTRAINT fkWorklogTask FOREIGN KEY (taskId) REFERENCES task ON DELETE CASCADE

)

"""
        }
    }
}