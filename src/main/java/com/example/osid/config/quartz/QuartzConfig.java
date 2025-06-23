// package com.example.osid.config.quartz;
//
// import org.quartz.JobBuilder;
// import org.quartz.JobDataMap;
// import org.quartz.JobDetail;
// import org.quartz.Trigger;
// import org.quartz.TriggerBuilder;
// import org.springframework.batch.core.configuration.JobLocator;
// import org.springframework.batch.core.launch.JobLauncher;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// @Configuration
// public class QuartzConfig {
//
// 	@Autowired
// 	private JobLauncher jobLauncher;
//
// 	@Autowired
// 	private JobLocator jobLocator;
//
// 	@Bean
// 	public JobDetail jobDetail() {
// 		JobDataMap jobDataMap = new JobDataMap();
// 		jobDataMap.put("jobName", "customJob");
//
// 		return JobBuilder.newJob(QuartzJobLauncher.class)
// 			.withIdentity("customJob")
// 			.setJobData(jobDataMap)
// 			.storeDurably()
// 			.build();
//
// 	}
//
// 	@Bean
// 	public Trigger jobTrigger() {
// 		// 	SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
// 		// 		.simpleSchedule()
// 		// 		.withIntervalInSeconds(10)
// 		// 		.repeatForever();
// 		//
// 		// 	return TriggerBuilder
// 		// 		.newTrigger()
// 		// 		.forJob(jobDetail())
// 		// 		.withIdentity("jobTrigger")
// 		// 		.withSchedule(scheduleBuilder)
// 		// 		.build();
//
// 		// Cron: "0 0 0 * * ?" → 매일 0시 0분 0초
// 		return TriggerBuilder
// 			.newTrigger()
// 			.forJob(jobDetail())
// 			.withIdentity("jobTrigger")
// 			.withSchedule(
// 				org.quartz.CronScheduleBuilder.cronSchedule("0 15 4 * * ?")
// 			)
// 			.build();
// 	}
// }
//
