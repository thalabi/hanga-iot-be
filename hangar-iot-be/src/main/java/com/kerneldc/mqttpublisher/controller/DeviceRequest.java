package com.kerneldc.mqttpublisher.controller;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class DeviceRequest {

	@NotBlank
	private String deviceName;
}
