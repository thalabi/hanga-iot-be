package com.kerneldc.hangariot.mqtt.service;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.kerneldc.hangariot.controller.Device;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeviceResponse {

	@JsonUnwrapped
	private Device device;
}
