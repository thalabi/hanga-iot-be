package com.kerneldc.hangariot.controller;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class TelePeriodRequest {

	@NotBlank(message = "Device name is missing")
	private String deviceName;
	@NotBlank(message = "TelePeriod is missing")
	@Min(value = 1, message = "TelePeriod cannot be less than one")
	private String telePeriod;
}
