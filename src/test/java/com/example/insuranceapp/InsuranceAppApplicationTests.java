package com.example.insuranceapp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
class InsuranceAppApplicationTests {

	@BeforeAll
	public static void setup() {
		System.setProperty("ELASTICSEARCH_HOST", "http://mocked-host:9200");
	}

}
