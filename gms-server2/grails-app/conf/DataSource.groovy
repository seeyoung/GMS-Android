dataSource {
    pooled = true
    driverClassName = "org.hsqldb.jdbc.JDBCDriver"
//    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
//	driverClassName = "oracle.jdbc.driver.OracleDriver"
//	username = "SAFETY119M"
//	password = "SAFETY119M"
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
//    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
}

// environment specific settings
environments {
    development {
        dataSource {
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
//          url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE;MODE=Oracle"
			url = "jdbc:hsqldb:hsql://localhost:9001/gmsdb;sql.syntax_ora=true"
//			logSql = true
//			formatSql = true
//			dbCreate = "update"
//			url = "jdbc:oracle:thin:@106.246.233.138:1521/XE"
			properties {
		        maxActive = 20
		        maxIdle = 1
		        minIdle = 1
		        initialSize = 1
		        minEvictableIdleTimeMillis = 60000
		        timeBetweenEvictionRunsMillis = 60000
		        maxWait = 10000
		    }
        }
    }
    test {
        dataSource {
//            dbCreate = "update"
//            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
    production {
        dataSource {
			dbCreate = "validate" // one of 'create', 'create-drop', 'update', 'validate', ''
//			url = "jdbc:hsqldb:hsql://localhost:9001/gmsdb;sql.syntax_ora=true"
//			properties {
//		        maxActive = 20
//		        maxIdle = 1
//		        minIdle = 1
//		        initialSize = 1
//		        minEvictableIdleTimeMillis = 60000
//		        timeBetweenEvictionRunsMillis = 60000
//		        maxWait = 10000
//		    }
//			dialect = 'org.hibernate.dialect.Oracle10gDialect'
			jndiName = "java:comp/env/jdbc/gmsdb"
			dialect = 'org.hibernate.dialect.HSQLDialect'
        }
    }
}
