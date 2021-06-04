package com.ashish.jobscheduler;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableBatchProcessing
@SpringBootApplication
public class JobSchedulerApplication {

	public static void main(String[] args)
	{
		SpringApplication.run(JobSchedulerApplication.class, args);
	    System.out.println("India");
	}

}
