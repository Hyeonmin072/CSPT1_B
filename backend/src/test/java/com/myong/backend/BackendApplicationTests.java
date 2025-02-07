package com.myong.backend;

import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.repository.UserRepository;
import com.myong.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest

class BackendApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	void contextLoads() {
		System.out.println("Hello World");
		System.out.println("Hello World2");
		System.out.println("Hello World3");
	}

	@Test
	void insertTestUser(){
		String name = "테스트2";
		String email = "mild11361@naver.com";
		String pwd = "aaa1234";
		String tel = "010-1234-1234";
		LocalDate birthDate = LocalDate.of(1999,11,27);
		Gender gender = Gender.MALE;
		String address = "스울";
		Optional<User> ou = userRepository.findByEmail(email);

		if(!ou.isPresent()){
			User user = new User(name,email,pwd,tel,birthDate,gender,address);
			userRepository.save(user);
		}

	}



}
