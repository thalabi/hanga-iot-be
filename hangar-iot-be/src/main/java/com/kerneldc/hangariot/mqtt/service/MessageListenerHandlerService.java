package com.kerneldc.hangariot.mqtt.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kerneldc.hangariot.exception.ApplicationException;
import com.kerneldc.hangariot.mqtt.topic.TopicHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageListenerHandlerService implements MessageHandler {

	@Value("${websocket.topics.prefix:/topic}")
	private String websocketTopicsPrefix;

	private final LastCommandResultCache lastCommandResultCache;
	private final ObjectMapper objectMapper;
	private final SimpMessagingTemplate webSocket;
	private final DeviceService deviceService;
	private final TopicHelper topicHelper;
	
	private String lineSeparator = System.getProperty("line.separator");
	
	@Override
	public void handleMessage(Message<?> messageObject) throws MessagingException {
		var topic = (String)messageObject.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
		var timestamp = (long)messageObject.getHeaders().get(MessageHeaders.TIMESTAMP); 
		var message = (String)messageObject.getPayload();
		
		LOGGER.info("Message [{}]{} arrived in topic [{}]", message, lineSeparator, topic);
		
		// message in POWER topic is not in json format
		if (StringUtils.endsWith(topic, "/POWER")) {
			message = transformPowerMessageToJson(message);
		}
		if (StringUtils.endsWith(topic, "/LWT")) {
			message = transformLwtMessageToJson(message);
		}
		
		try {
			message = addTimeStampToMessage(timestamp, message);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new MessagingException("Error parsing message string as a JSON object", NestedExceptionUtils.getMostSpecificCause(e));
		}		
		
		if (StringUtils.endsWith(topic, "/RESULT")) {
			try {
				lastCommandResultCache.setCommandResult(topic, message);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new MessagingException("Failed to add message to cache.", e);
			}
		} else {
			if (StringUtils.endsWith(topic, "/LWT")) {
				try {
				setDeviceState(topic, message);
				} catch (JsonProcessingException | ApplicationException e) {
					e.printStackTrace();
					throw new MessagingException("Failed to set device state.", e);
				}
			}
		}
		
		var webSocketTopic = websocketTopicsPrefix + "/state-and-telemetry/" + topic;
		webSocket.convertAndSend(webSocketTopic, message);
		
		LOGGER.info("Message [{}],{} in topic [{}] added to WebSocket topic [{}]", message, lineSeparator, topic, webSocketTopic);
	}

	private String addTimeStampToMessage(long timestamp, String message) throws JsonProcessingException {
		var jsonMessage = objectMapper.readTree(message);
		((ObjectNode) jsonMessage).put("timestamp", timestamp);
		return objectMapper.writeValueAsString(jsonMessage);
	}

	
	private String transformPowerMessageToJson(String topic) {
		return topic.replaceAll("^(.*)$", "{\"POWER\":\"$1\"}");
	}
	private String transformLwtMessageToJson(String topic) {
		return topic.replaceAll("^(.*)$", "{\"LWT\":\"$1\"}");
	}

	private void setDeviceState(String topicString, String message) throws JsonProcessingException, ApplicationException {
		var deviceName = topicHelper.getDeviceName(topicString);
//		Device device = deviceService.getDeviceList().stream().filter(d -> StringUtils.equals(d.getName(), deviceName)).findAny().orElse(null);
//		if (device == null) {
//			throw new MessagingException(String.format("Cannot find device name [%s]", deviceName));
//		}
//		var jsonMessage = objectMapper.readTree(message);
//		device.setState(jsonMessage.get("LWT").textValue());
//		device.setStateChangeTimestamp(jsonMessage.get("timestamp").longValue());
		
//		var state = jsonMessage.get("LWT").textValue();
		lastCommandResultCache.setConnectionState(deviceName, message);
	}
}
