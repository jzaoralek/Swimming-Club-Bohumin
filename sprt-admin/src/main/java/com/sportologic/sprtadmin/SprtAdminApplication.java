package com.sportologic.sprtadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EntityScan("com.sportologic.*")
public class SprtAdminApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SprtAdminApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SprtAdminApplication.class);
	}

	@Bean(name = "restTemplate")
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
