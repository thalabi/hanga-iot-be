package com.kerneldc.hangariot.mqtt.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Lwt {
	@JsonProperty("LWT")
	private String lwt;
	private Long timestamp;
}
