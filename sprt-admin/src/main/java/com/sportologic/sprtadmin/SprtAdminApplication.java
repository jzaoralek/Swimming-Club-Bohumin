package com.sportologic.sprtadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.sportologic.*")
public class SprtAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(SprtAdminApplication.class, args);
	}
}
