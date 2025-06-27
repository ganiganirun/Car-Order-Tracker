package com.example.osid.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class WaitingJobScheduler {
	private final JobLauncher jobLauncher;
	private final JobRegistry jobRegistry;

	public WaitingJobScheduler(JobLauncher jobLauncher, JobRegistry jobRegistry) {
		this.jobLauncher = jobLauncher;
		this.jobRegistry = jobRegistry;
	}

	@Scheduled(cron = "0 0 12 * * *", zone = "Asia/Seoul")
	public void runCustomJob() throws Exception {

		System.out.println("Wationg schedule start");

		// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		// String date = dateFormat.format(new Date());

		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));

		JobParameters jobParameters = new JobParametersBuilder()
			.addString("date", date)
			.toJobParameters();

		jobLauncher.run(jobRegistry.getJob("customJob"), jobParameters);
	}
}
