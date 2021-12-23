package com.kerneldc.mqttpublisher.mqtt.service;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.kerneldc.mqttpublisher.controller.Device;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeviceResponse {

	@JsonUnwrapped
	private Device device;
}
