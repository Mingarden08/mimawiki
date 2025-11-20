package com.mimawiki.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling // ✅ 이게 꼭 있어야 메모리 청소가 됩니다!
@SpringBootApplication
public class MimawikiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MimawikiApplication.class, args);
		log.info("\n\n======================================== ApiApplication started ========================================\n\n");
	}

}
