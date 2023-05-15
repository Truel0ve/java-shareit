package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {ShareItServer.class})
public class ShareItServer {

	public static void main(String[] args) {
		SpringApplication.run(ShareItServer.class, args);
	}

}
