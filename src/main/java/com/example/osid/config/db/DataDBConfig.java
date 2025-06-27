package com.example.osid.config.db;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
	basePackages = {
		"com.example.osid.domain",
		"com.example.osid.common",
		"com.example.osid.event"
	},
	entityManagerFactoryRef = "dataEntityManager",
	transactionManagerRef = "dataTransactionManager"
)
public class DataDBConfig {

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource-data")
	public DataSource dataDBSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean dataEntityManager() {

		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

		em.setDataSource(dataDBSource());
		em.setPackagesToScan(
			new String[] {"com.example.osid.domain", "com.example.osid.common", "com.example.osid.event"});
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

		HashMap<String, Object> properties = new HashMap<>();
		properties.put("hibernate.physical_naming_strategy",
			"org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
		properties.put("hibernate.hbm2ddl.auto", "update");
		// properties.put("hibernate.show_sql", "true");
		properties.put("hibernate.format_sql", "true");
		properties.put("hibernate.use_sql_comments", "true");
		properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
		properties.put("hibernate.jdbc.fetch_size", "1000");

		em.setJpaPropertyMap(properties);

		return em;
	}

	@Bean(name = "dataTransactionManager")
	public PlatformTransactionManager dataTransactionManager() {

		JpaTransactionManager transactionManager = new JpaTransactionManager();

		transactionManager.setEntityManagerFactory(dataEntityManager().getObject());

		return transactionManager;
	}

}
