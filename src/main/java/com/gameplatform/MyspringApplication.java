package com.gameplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Async;

@SpringBootApplication
@Async
public class MyspringApplication {

	public static void main(String[] args) {
		 SpringApplication.run(MyspringApplication.class, args);
		 
	}

}
