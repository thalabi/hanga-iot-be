package com.kerneldc.mqttpublisher.mqtt.service;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageListenerHandlerService implements MessageHandler {

	@Value("${websocket.topics.prefix:/topic}")
	private String WEBSOCKET_TOPICS_PREFIX;

	private final StateAndTelemetryCache stateAndTelemetryCache;
	private final ObjectMapper objectMapper;
	private final SimpMessagingTemplate webSocket;
	
	@Override
	public void handleMessage(Message<?> message) throws MessagingException {
		var topicString = (String)message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
		var timestamp = (long)message.getHeaders().get(MessageHeaders.TIMESTAMP); 
		var messageString = (String)message.getPayload();
		
		LOGGER.info("Message [{}] arrived in topic [{}]", messageString, topicString);
		
		if (StringUtils.endsWith(topicString, "/POWER")) {
			messageString = transformPowerMessageToJson(messageString);
		}
		
		try {
			messageString = addTimeStampToMessage(timestamp, messageString);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new MessagingException("Error parsing message string as a JSON object", NestedExceptionUtils.getMostSpecificCause(e));
		}		
		
		

		stateAndTelemetryCache.setData(topicString, messageString);
		
		var webSocketTopic = WEBSOCKET_TOPICS_PREFIX + "/state-and-telemetry/" + topicString;
		webSocket.convertAndSend(webSocketTopic, messageString);
		
		LOGGER.info("Message [{}], topic [{}] added to cache and WebSocket topic [{}]", messageString, topicString, webSocketTopic);
	}

	private String addTimeStampToMessage(long timestamp, String message) throws JsonProcessingException {
		var jsonMessage = objectMapper.readTree(message);
		((ObjectNode) jsonMessage).put("timestamp", timestamp);
		return objectMapper.writeValueAsString(jsonMessage);
	}

	
	private String transformPowerMessageToJson(String topicString) {
		return topicString.replaceAll("^(.*)$", "{\"POWER\":\"$1\"}");
	}

}
