package com.kerneldc.hangariot.mqtt.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kerneldc.hangariot.controller.Device;
import com.kerneldc.hangariot.springconfig.DeviceListPropertyHolder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceService {

	private final DeviceListPropertyHolder deviceListPropertyHolder;
	
	public List<Device> getDeviceList() {
		return deviceListPropertyHolder.getDeviceList();
	}
	
	public List<String> getDeviceNameList() {
		return deviceListPropertyHolder.getDeviceList().stream().map(Device::getName).toList();
	}
}
