import groovy.sql.Sql

class BootstrapGsql {

//    def workspaceService
//    def taskService

    def init = { String dataSourceName = 'default', Sql sql ->
//        workspaceService.doInsert ('prova1', 'prova uno')
//
//        def prova1 = sql.executeInsert("INSERT INTO workspace (name, description) VALUES ('prova1','prova uno')")
//        def prova2 = sql.executeInsert("INSERT INTO workspace (name, description) VALUES ('prova2','prova due')")
//
//
//        def prova1root = sql.executeInsert("INSERT INTO task (workspaceId, parentId, title, description) VALUES (?,null, 'prova 1', 'bla bla bla')", [prova1[0][0]])
//        def prova1child1 = sql.executeInsert("INSERT INTO task (workspaceId, parentId, title, description) VALUES (?, ?, 'prova 1', 'bla bla bla')", [prova1[0][0], prova1root[0][1]])
    }

    def destroy = { String dataSourceName = 'default', Sql sql ->
    }
} 
