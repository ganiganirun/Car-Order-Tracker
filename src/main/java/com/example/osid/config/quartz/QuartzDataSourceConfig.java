// package com.example.osid.config.quartz;
//
// import javax.sql.DataSource;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// import com.zaxxer.hikari.HikariDataSource;
//
// @Configuration
// public class QuartzDataSourceConfig {
//
// 	@Value("${LOCAL_DB_USERNAME}")
// 	private String name;
//
// 	@Value("${LOCAL_DB_PASSWORD}")
// 	private String password;
//
// 	@Bean(name = "quartzDataSource")
// 	@QuartzDataSource
// 	public DataSource quartzDataSource() {
// 		HikariDataSource ds = new HikariDataSource();
// 		ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
// 		ds.setJdbcUrl("jdbc:mysql://localhost:3306/quartz_db");
// 		ds.setUsername(name); // <- 환경변수로 바꿔도 됨
// 		ds.setPassword(password);
// 		return ds;
// 	}
// }
