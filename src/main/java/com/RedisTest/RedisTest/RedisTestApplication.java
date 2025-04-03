package com.RedisTest.RedisTest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class RedisTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisTestApplication.class, args);
	}


	@Bean
	CommandLineRunner init(UserRepository userRepository) {
		return args -> {
			if (userRepository.count() == 0) {
				List<User> users = new ArrayList<>();
				for (long i = 1; i <= 10000; i++) {
					users.add(User.builder()
							.name("User " + i)
							.email("user" + i + "@test.com")
							.build());
				}
				userRepository.saveAll(users);
				System.out.println("✅ 1만 명 유저 삽입 완료");
			}
		};
	}


}
