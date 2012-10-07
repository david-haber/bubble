package com.bubble.config;

import static org.junit.Assert.*;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestMainConfig {
	
	static MainConfig mainConfig;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mainConfig = new MainConfig();
	}

	@Test
	public void testConnectsToDB() throws SQLException {
		DataSource ds = mainConfig.dataSource();
		assertNotNull(ds.getConnection());
	}

}
