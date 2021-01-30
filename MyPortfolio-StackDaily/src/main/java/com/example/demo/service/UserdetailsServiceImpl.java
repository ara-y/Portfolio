package com.example.demo.service;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.example.demo.model.SiteUser;
import com.example.demo.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserdetailsServiceImpl implements UserDetailsService {
private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String name)throws UsernameNotFoundException{
		
		SiteUser user = userRepository.findByName(name);
		
		if(user == null) {
			System.out.println("エラーでござる");
			throw new UsernameNotFoundException(name + "not found");
		}
		return createUserDetails(user);
	}
	
	public User createUserDetails(SiteUser user) {
		Set<GrantedAuthority> grantedAuthories = new HashSet<>();
		grantedAuthories.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
		
		return new User(user.getName(), user.getPassword(), grantedAuthories);
	}
}
