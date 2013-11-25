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

    changeSet(id:'task-worklogs-fix', author: 'davidecavestro') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            """

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
    worklog wl RIGHT OUTER JOIN task tk ON (wl.taskid=tk.id) WHERE (wl.deleted IS NULL OR wl.deleted<>TRUE) AND tk.deleted<>TRUE
GROUP BY
    tk.id
);

"""
        }
    }
}