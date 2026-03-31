package br.com.sensedia;

import org.springframework.boot.SpringApplication;

public class TestConsentSampleApplication {

	public static void main(String[] args) {
		SpringApplication.from(ConsentSampleApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
