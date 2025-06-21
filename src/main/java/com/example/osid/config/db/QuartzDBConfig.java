// package com.example.osid.config.db;
//
// import javax.sql.DataSource;
//
// import org.springframework.boot.context.properties.ConfigurationProperties;
// import org.springframework.boot.jdbc.DataSourceBuilder;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.jdbc.datasource.DataSourceTransactionManager;
// import org.springframework.transaction.PlatformTransactionManager;
//
// @Configuration
// public class QuartzDBConfig {
//
// 	@Bean(name = "quartzDataSource")
// 	@ConfigurationProperties(prefix = "spring.datasource-quartz")
// 	public DataSource quartzDataSource() {
// 		return DataSourceBuilder.create().build();
// 	}
//
// 	@Bean(name = "quartzTransactionManager")
// 	public PlatformTransactionManager quartzTransactionManager() {
// 		return new DataSourceTransactionManager(quartzDataSource());
// 	}
// }
