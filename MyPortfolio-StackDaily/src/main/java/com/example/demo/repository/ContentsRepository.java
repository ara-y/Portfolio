package com.example.demo.repository;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.Contents;

public interface ContentsRepository extends CrudRepository<Contents, Integer> {
	Collection<Contents> findByContributorAndDdate(String contributor,LocalDate date);
	List<Contents> deleteByContributorAndDdate(String contributor,LocalDate date);
}
