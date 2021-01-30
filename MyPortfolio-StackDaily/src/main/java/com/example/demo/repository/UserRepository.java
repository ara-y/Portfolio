package com.example.demo.repository;
import org.springframework.data.repository.CrudRepository;
import com.example.demo.model.SiteUser;

public interface UserRepository  extends CrudRepository<SiteUser, Integer> {
	SiteUser findByName(String username);
	boolean existsByName(String username);
}
