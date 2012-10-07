package com.bubble.config;

/**
 * Main configurations for application
 * e.g. database configuration
 */

import javax.inject.Inject;
import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class MainConfig {
	
	@Inject
		
	@Bean
	public DataSource dataSource() {
		
		PGSimpleDataSource pg = new PGSimpleDataSource();
		pg.setServerName("db.doc.ic.ac.uk");
		pg.setDatabaseName("g1136221_u");
		pg.setPortNumber(5432);
		pg.setSsl(true);
		pg.setSslfactory("org.postgresql.ssl.NonValidatingFactory");
		pg.setUser("g1136221_u");
		pg.setPassword("J2ezbmSgff");
		
		/* Config to connect to local db */
//		PGSimpleDataSource pg = new PGSimpleDataSource();
//		pg.setServerName("localhost");
//		pg.setDatabaseName("mydb");
//		pg.setPortNumber(5432);
//		pg.setSsl(true);
//		pg.setSslfactory("org.postgresql.ssl.NonValidatingFactory");
//		pg.setUser("postgres");
//		pg.setPassword("happyxmas");
		
		return pg;
	}

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}


}
