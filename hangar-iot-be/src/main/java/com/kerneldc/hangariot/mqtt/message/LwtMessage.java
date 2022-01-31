package com.kerneldc.hangariot.mqtt.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LwtMessage {
	@JsonProperty("LWT")
	private String lwt;
	private Long timestamp;
}
