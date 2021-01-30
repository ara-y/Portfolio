package com.example.demo.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Getter
@Setter
@Entity
public class Registerword {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	
	@NotBlank
	@Size(max = 100)
	private String word;
	
	@NotBlank
	private String partofspeech;
	
	@NotBlank
	@Size(max = 1000)
	private String mean;
	
	@NotBlank
	private String date;
	
}
