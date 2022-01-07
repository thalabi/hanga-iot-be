package com.kerneldc.hangariot.controller;

import lombok.Data;

@Data
public class Device {

	private enum DeviceTypeEnum {PLUG}
	
	private String name;
	private String description;
	private String location;
	private DeviceTypeEnum deviceType;
	private Boolean telemetry;
	private String iotDeviceMake;
	private String iotDeviceModel;
}
