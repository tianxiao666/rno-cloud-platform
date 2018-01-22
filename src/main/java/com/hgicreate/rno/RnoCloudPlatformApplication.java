package com.hgicreate.rno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RnoCloudPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(RnoCloudPlatformApplication.class, args);
	}
}
