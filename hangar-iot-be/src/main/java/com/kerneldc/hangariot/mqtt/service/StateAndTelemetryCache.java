package com.kerneldc.hangariot.mqtt.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StateAndTelemetryCache {
	
	private Map<String, String> stateAndTelemetryMapCache = new HashMap<>();
	
	public void setData(String topic, String message) {
		if (StringUtils.isEmpty(topic) || message == null) {
			LOGGER.warn("Adding an empty topic or an empty value. Topic: [{}], value: [{}]");
		}
		stateAndTelemetryMapCache.put(topic, message);
	}

	public String getData(String topic) {
		return stateAndTelemetryMapCache.get(topic);
	}
	
	public void dumpCache() {
		LOGGER.info("Dump of stateAndTelemetryMapCache:");
		for (var entry: stateAndTelemetryMapCache.entrySet()) {
			LOGGER.warn("key: [{}], value: [{}]", entry.getKey(), entry.getValue());
		}
	}
}
