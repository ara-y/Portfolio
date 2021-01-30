package com.example.demo.model;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class Reminder {
	
	@NotBlank
	String notification;
	
	@NotBlank
	int setting = 0;
	
	@NotBlank
	int counter;
	
}
