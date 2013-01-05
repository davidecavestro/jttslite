/*
 * Copyright (c) 2012, the original author or authors.
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

package jttslite
import groovy.sql.Sql

/**
 * Business logic for Dictionary entities.
 *
 * @author Davide Cavestro
 */
class DictionaryService implements Serializable {
    /**
     * Writes s a text property.
     *
     * @param key the property key.
     * @param value the property value.
     */
    def doSaveString(def key, String value=null) {
        doDelete (key)
        withSql { String dataSourceName, Sql sql ->
            sql.executeInsert('INSERT INTO dictionary (key, stringValue) VALUES (?,?)',
                    [key, value])
        }
    }

    /**
     * Writes a number property.
     *
     * @param key the property key
     * @param value the property value
     */
    def doSaveLong(def key, Long value=null) {
        doDelete (key)
        withSql { String dataSourceName, Sql sql ->
            sql.executeInsert('INSERT INTO dictionary (key, longValue) VALUES (?,?)',
                    [key, value])
        }
    }

    /**
     * Writes a boolean property.
     *
     * @param key the property key
     * @param value the property value
     */
    def doSaveBoolean(def key, Boolean value=null) {
        doDelete (key)
        withSql { String dataSourceName, Sql sql ->
            sql.executeInsert('INSERT INTO dictionary (key, booleanValue) VALUES (?,?)',
                    [key, value])
        }
    }

    public int doDelete(def propkey) {
        withSql { String dataSourceName, Sql sql ->
            sql.executeUpdate('DELETE FROM dictionary WHERE key=?',
                    [propkey])
        }
    }

    String getStringValue(def propkey) {
        withSql { String dataSourceName, Sql sql ->
            sql.firstRow('SELECT * FROM dictionary WHERE key=?', [propkey])?.stringValue
        }
    }
    Long getLongValue(def propkey) {
        withSql { String dataSourceName, Sql sql ->
            sql.firstRow('SELECT * FROM dictionary WHERE key=?', [propkey])?.longValue
        }
    }
    Boolean getBooleanValue(def propkey) {
        withSql { String dataSourceName, Sql sql ->
            sql.firstRow('SELECT * FROM dictionary WHERE key=?', [propkey])?.booleanValue
        }
    }
}