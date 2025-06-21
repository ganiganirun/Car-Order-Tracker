// package com.example.osid.config.quartz;
//
// import javax.sql.DataSource;
//
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.context.annotation.Bean;
// import org.springframework.core.io.ClassPathResource;
// import org.springframework.jdbc.datasource.init.DataSourceInitializer;
// import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
//
// public class QuartzSchemaInitializerConfig {
// 	@Bean
// 	public DataSourceInitializer quartzSchemaInitializer(@Qualifier("quartzDataSource") DataSource quartzDataSource) {
// 		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
// 		populator.addScript(
// 			new ClassPathResource("quartz/tables_mysql_innodb.sql")); // 위치는 src/main/resources/quartz 에 둬야 함
// 		populator.setContinueOnError(false);
// 		populator.setIgnoreFailedDrops(true);
//
// 		DataSourceInitializer initializer = new DataSourceInitializer();
// 		initializer.setDataSource(quartzDataSource);
// 		initializer.setDatabasePopulator(populator);
// 		return initializer;
// 	}
//
// }
