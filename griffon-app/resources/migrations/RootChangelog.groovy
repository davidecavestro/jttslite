/*
 * Copyright (c) 2013, the original author or authors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

databaseChangeLog() {
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
    start DATETIME NOT NULL, // start time
    amount LONG, //duration in millis
    comment VARCHAR(10000),

    CONSTRAINT fkWorklogTask FOREIGN KEY (taskId) REFERENCES task ON DELETE CASCADE

);

"""
        }
    }
    changeSet(id:'20130201-addtimestamps', author: 'davidecavestro') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
"""
ALTER TABLE dictionary ADD COLUMN ts TIMESTAMP AS CURRENT_TIMESTAMP();
ALTER TABLE workspace ADD COLUMN ts TIMESTAMP AS CURRENT_TIMESTAMP();
ALTER TABLE task ADD COLUMN ts TIMESTAMP AS CURRENT_TIMESTAMP();
ALTER TABLE worklog ADD COLUMN ts TIMESTAMP AS CURRENT_TIMESTAMP();
"""
        }
    }
    /*
    changeSet(id:'20130201-addtaskdurations', author: 'davidecavestro') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            """
ALTER TABLE task ADD COLUMN localAmount LONG; //direct duration in millis
ALTER TABLE task ADD COLUMN globalAmount LONG; //subtree + direct duration in millis

"""
        }
    } */

    changeSet(id:'20130203-addtaskamounts', author: 'davidecavestro') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            """\
CREATE VIEW task_worklogs AS
(
SELECT
    tk.id,
    SUM (wl.amount) AS localAmount,
    MIN (
        SELECT
            SUM (wl1.amount)
        FROM
            worklog wl1 INNER JOIN task tk1 ON (wl1.taskId=tk1.id) WHERE tk1.treeCode LIKE CONCAT (tk.treeCode, '%')) AS globalAmount
FROM
    worklog wl RIGHT OUTER JOIN task tk ON (wl.taskid=tk.id)
GROUP BY
    tk.id
);
"""
        }
    }
    changeSet(id:'20130206-addworklogview', author: 'davidecavestro') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            """\
CREATE VIEW worklog_period AS
(
SELECT
    id, start, DATEADD ('MS', amount, start) AS finish, amount
FROM worklog
);
"""
        }
    }
    include(file: 'migrations/20130301-add-delete-fields.groovy', relativeToChangelog: false)


}