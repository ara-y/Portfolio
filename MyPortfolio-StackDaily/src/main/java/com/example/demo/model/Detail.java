package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import java.time.LocalDate;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Detail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Size(min=2, max=120)
	private String whatIDid;
	
	@Size(max=3000)
	private String comment;
	
	private String contributor;
	
	private LocalDate doneDate;
}
