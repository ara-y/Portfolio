package com.example.demo.UploadingSetting;

import org.springframework.web.multipart.MultipartFile;

public class UploadingSetting {
	private MultipartFile file;
	
	public MultipartFile getFile() {
		return file;
	}
	
	public void setFile(MultipartFile file) {
		this.file = file;
	}
}
