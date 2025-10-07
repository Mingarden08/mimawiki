package com.mimawiki.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class MimawikiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MimawikiApplication.class, args);
		log.info("\n\n======================================== ApiApplication started ========================================\n\n");
	}

}
