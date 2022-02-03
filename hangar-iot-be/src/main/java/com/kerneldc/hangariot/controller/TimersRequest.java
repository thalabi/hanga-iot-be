package com.kerneldc.hangariot.controller;


import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kerneldc.hangariot.mqtt.result.timer.TimersResult;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@JsonDeserialize(using = TimersRequestDeserializer.class)
public class TimersRequest extends TimersResult {

	@NotBlank(message = "Device name is missing")
	private String deviceName;
	
	private Boolean timersModified;
	
	private Boolean[] timerModifiedArray = new Boolean[16];
}
