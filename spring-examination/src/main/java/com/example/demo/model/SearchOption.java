package com.example.demo.model;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class SearchOption {
	
	
	private String option_main ="無";
	
	
	private String option_sub ="0";
	
	
	private String searchword_s;
	
	@NotBlank
	private String toEdit = "初期値";
	
	private String toMove;
	
	private String searchdate_s;
	
	//for editing word
	@NotBlank
	private String toId;
	
	
}
