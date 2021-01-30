package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.demo.model.SiteUser;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserdetailsServiceImpl;
import com.example.demo.util.Role;


import org.junit.jupiter.api.Test;


@SpringBootTest
@Transactional
class UserDetailServiceImplTest {
	
	@Autowired
	UserRepository repository;
	
	@Autowired
	UserdetailsServiceImpl service;
	
	@Test
	@DisplayName("ユーザ名が存在する時にユーザ詳細を取得することを期待します")
	void whenUsernameExists_expectToGetUserDetails() {
		SiteUser user = new SiteUser();
		user.setName("荒");
		user.setPassword("password");
		user.setRole(Role.USER.name());
		repository.save(user);
		
		UserDetails actual = service.loadUserByUsername("荒");
		
		assertEquals(user.getName(), actual.getUsername());
	}
	
	@Test
	@DisplayName("ユーザ名が存在しない時に例外を投げます")
	void whenUsernameDoesNotExsist_throwException() {
		assertThrows(UsernameNotFoundException.class, 
				() -> service.loadUserByUsername("武田"));
	}

}
