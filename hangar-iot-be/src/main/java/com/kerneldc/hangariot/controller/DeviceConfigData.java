package com.kerneldc.hangariot.controller;

import lombok.Data;

@Data
public class DeviceConfigData {
	private Float latitudeDegrees;
	private Float longitudeDegrees;
	private String timezoneOffset;
	private String timeDst;
	private String timeStd;
}