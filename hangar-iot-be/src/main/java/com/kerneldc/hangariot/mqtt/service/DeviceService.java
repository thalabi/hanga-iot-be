package com.kerneldc.hangariot.mqtt.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kerneldc.hangariot.controller.Device;
import com.kerneldc.hangariot.springconfig.DeviceListPropertyHolder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceService {

	private final DeviceListPropertyHolder deviceListPropertyHolder;
	@Value("${client-exposed.mqtt.commands}")
	private String[] commands;
	
	public List<Device> getDeviceList() {
		return deviceListPropertyHolder.getDeviceList();
	}
	
	public List<String> getDeviceNameList() {
		return deviceListPropertyHolder.getDeviceList().stream().map(Device::getName).toList();
	}

	public List<String> getCommandList() {
		return Arrays.asList(commands);
	}
}
