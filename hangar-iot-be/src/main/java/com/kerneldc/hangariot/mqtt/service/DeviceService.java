package com.kerneldc.hangariot.mqtt.service;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
	
	public Device getDevice(String name) {
		return getDeviceList().stream().filter(device -> StringUtils.equals(device.getName(), name)).findAny().orElse(null);
	}
	
	public List<String> getDeviceNameList() {
		return deviceListPropertyHolder.getDeviceList().stream().map(Device::getName).toList();
	}

	public List<String> getCommandList() {
		return Arrays.asList(commands);
	}
}
