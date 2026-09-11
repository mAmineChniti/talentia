package com.amani.Talent.IA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TalentIaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TalentIaApplication.class, args);
	}

}
