// package com.example.osid.config.quartz;
//
// import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
// import org.springframework.boot.autoconfigure.quartz.QuartzTransactionManager;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.io.ClassPathResource;
// import org.springframework.jdbc.datasource.DataSourceTransactionManager;
// import org.springframework.jdbc.datasource.init.DataSourceInitializer;
// import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
//
// @Configuration
// public class DataSourceConfig {
//
// 	@QuartzDataSource
// 	DataSource dataSource;
//
// 	public DataSourceConfig(DataSource dataSource) {
// 		this.dataSource = dataSource;
// 	}
//
// 	@Bean
// 	@QuartzTransactionManager
// 	public DataSourceTransactionManager transactionManager() {
// 		return new DataSourceTransactionManager(dataSource);
// 	}
//
// 	@Bean
// 	public DataSourceInitializer databasePoulator() {
// 		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
// 		populator.addScript(new ClassPathResource("org/springframework/batch/core/schema-drop-mysql.sql"));
// 		populator.addScript(new ClassPathResource("org/springframework/batch/core/schema-mysql.sql"));
// 		populator.setContinueOnError(false);
// 		populator.setIgnoreFailedDrops(false);
// 		DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
// 		dataSourceInitializer.setDataSource(dataSource);
// 		dataSourceInitializer.setDatabasePopulator(populator);
// 		return dataSourceInitializer;
// 	}
// }
