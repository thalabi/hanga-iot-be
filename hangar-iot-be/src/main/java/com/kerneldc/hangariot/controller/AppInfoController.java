package com.kerneldc.hangariot.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("appInfoController")
public class AppInfoController {

	@Value("${build.version}")
	private String buildVersion;

	@Value("${build.timestamp}")
	private String buildTimestamp;

	@GetMapping("/getBuildInfo")
	public String getBuildInfo() {
		return buildVersion + "_" + buildTimestamp;
	}
}
