package com.kerneldc.hangariot.mqtt.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Power {
	@JsonProperty("POWER")
	private String power;
	private Long timestamp;
}
