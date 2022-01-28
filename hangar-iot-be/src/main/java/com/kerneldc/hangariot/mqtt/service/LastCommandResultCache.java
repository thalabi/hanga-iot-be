package com.kerneldc.hangariot.mqtt.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kerneldc.hangariot.exception.ApplicationException;
import com.kerneldc.hangariot.mqtt.result.AbstractBaseResult;
import com.kerneldc.hangariot.mqtt.result.CommandEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LastCommandResultCache {
	
	private enum DeviceStateEnum {
		ONLINE, OFFLINE
	}

	private final ObjectMapper objectMapper;
	private Map<DeviceNameAndCommandEnum, AbstractBaseResult> resultTopicCache = Collections.synchronizedMap(new HashMap<>());
	private Map<String, String> deviceConnectionStateMap = new HashMap<>();
	
	private record DeviceNameAndCommandEnum(String deviceName, CommandEnum commandEnum) {}

	public void setCommandResult(String topic, String message) throws JsonProcessingException {
		var deviceName = extractDeviceName(topic);
	    var commandEnum = getCommandEnum(message);
	    if (commandEnum == null) {
	    	LOGGER.warn("Message type of [{}] is not supported. Can't add it to cache", message);
	    	return;
	    }
		var result = objectMapper.readValue(message, commandEnum.getResultType());
		
		resultTopicCache.put(new DeviceNameAndCommandEnum(deviceName, commandEnum), result);
	}

	public AbstractBaseResult getCommandResult(String deviceName, CommandEnum commandEnum) {
		return resultTopicCache.get(new DeviceNameAndCommandEnum(deviceName, commandEnum));
	}

	private CommandEnum getCommandEnum(String message) throws JsonProcessingException {
		var jsonObject = objectMapper.readValue(message, ObjectNode.class);
		try {
			return Enum.valueOf(CommandEnum.class, jsonObject.fieldNames().next().toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private String extractDeviceName(String topic) {
		var p = Pattern.compile(".+/(.+)/(RESULT|LWT)");
		var m = p.matcher(topic);
		if (m.matches() && StringUtils.isNotEmpty(m.group(1))) {
			return m.group(1);
		} else {
			throw new IllegalStateException(String.format("Could not extract device name from topic [%s]", topic)); 
		}
	}

	public void dumpCache() {
		LOGGER.info("Dump of resultTopicCache:");
		for (var entry: resultTopicCache.entrySet()) {
			LOGGER.info("key: [{}], value: [{}]", entry.getKey(), entry.getValue());
		}
	}
	
	public String getConnectionState(String deviceName) {
		return deviceConnectionStateMap.get(deviceName);
	}
	public void setConnectionState(String deviceName, String lwtMessage) throws ApplicationException {
//		DeviceStateEnum state;
//		try {
//			state = DeviceStateEnum.valueOf(stateString.toUpperCase());
//		} catch (IllegalArgumentException e) {
//			throw new ApplicationException(String.format("Unable to find DeviceStateEnum with value %s", stateString.toUpperCase()), e);
//		}
		deviceConnectionStateMap.put(deviceName, lwtMessage);
	}

	@PreDestroy
	public void terminate() {
		resultTopicCache.clear();
		deviceConnectionStateMap.clear();
	}
	
}
