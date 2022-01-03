package com.kerneldc.hangariot.security.controller;

import com.kerneldc.hangariot.security.CustomUserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

	private CustomUserDetails customUserDetails;
	private String token;
	
}
