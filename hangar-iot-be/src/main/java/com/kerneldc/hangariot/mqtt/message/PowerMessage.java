package com.kerneldc.hangariot.mqtt.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PowerMessage {
	@JsonProperty("POWER")
	private String power;
	private Long timestamp;
}
