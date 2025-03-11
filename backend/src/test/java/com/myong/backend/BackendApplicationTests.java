package com.myong.backend;

import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.repository.DesignerRepository;
import com.myong.backend.repository.ShopRepository;
import com.myong.backend.repository.UserRepository;
import com.myong.backend.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest

class BackendApplicationTests {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ShopRepository shopRepository;
	@Autowired
	private DesignerRepository designerRepository;


//	@Test
//	void designerRegister(){
//
//		Optional<Designer> od = designerRepository.findByEmail("test123@naver.com");
//		Optional<Designer> od2 = designerRepository.findByEmail("2test123@naver.com");
//		Optional<Designer> od3 = designerRepository.findByEmail("3test123@naver.com");
//
//		Optional<Shop> oShop = shopRepository.findByEmail("rlawjggns12@gmail.com");
//		Optional<Shop> oShop2 = shopRepository.findByEmail("test123@gmail.com");
//
//		Designer designer1 = od.get();
//		Designer designer2 = od2.get();
//		Designer designer3 = od3.get();
//
//		Shop shop = oShop.get();
//		Shop shop2 = oShop2.get();
//
//		designer1.registerDesigner(shop);
//		designer2.registerDesigner(shop2);
//		designer3.registerDesigner(shop2);
//
//
//
//	}


//	@Test
//	void contextLoads() {
//		System.out.println("Hello World");
//		System.out.println("Hello World2");
//		System.out.println("Hello World3");
//	}
////
//	@Test
//	void insertTestUser(){
//		String name = "테스트2";
//		String email = "mild11361@naver.com";
//		String pwd = passwordEncoder.encode("aaa1234");
//		String tel = "010-1234-1234";
//		LocalDate birthDate = LocalDate.of(1999,11,27);
//		Gender gender = Gender.MALE;
//		String address = "스울";
//		Optional<User> ou = userRepository.findByEmail(email);
//
//		if(!ou.isPresent()){
//			User user = new User(name,email,pwd,tel,birthDate,gender,address);
//			userRepository.save(user);
//		}
//
//	}



}
