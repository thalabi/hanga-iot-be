package com.kerneldc.hangariot.mqtt.service;

import java.util.List;
import java.util.regex.Pattern;

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
import com.kerneldc.hangariot.mqtt.topic.TopicHelper;
import com.kerneldc.hangariot.mqtt.topic.TopicHelper.TopicSuffixEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageListenerHandlerService implements MessageHandler {

	@Value("${websocket.topics.prefix:/topic}")
	private String websocketTopicsPrefix;

	private final ApplicationCache applicationCache;
	private final ObjectMapper objectMapper;
	private final SimpMessagingTemplate webSocket;
	private final TopicHelper topicHelper;
	
	private String lineSeparator = System.getProperty("line.separator");
	
	@Override
	public void handleMessage(Message<?> messageObject) throws MessagingException {
		var fullTopic = (String)messageObject.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
		var timestamp = (long)messageObject.getHeaders().get(MessageHeaders.TIMESTAMP); 
		var message = (String)messageObject.getPayload();
		
		LOGGER.info("Message [{}]{} arrived in topic [{}]", message, lineSeparator, fullTopic);
		
		var topicSuffix = getTopicSuffix(fullTopic);
		// message in POWER & Lwt topics is not in json format
		if (topicSuffix.equals(TopicSuffixEnum.POWER) || topicSuffix.equals(TopicSuffixEnum.LWT)) {
			message = transformMessageToJson(message, topicSuffix);
		}
		
		// add timestamp to all messages
		try {
			message = addTimeStampToMessage(timestamp, message);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new MessagingException("Error parsing message string as a JSON object", NestedExceptionUtils.getMostSpecificCause(e));
		}		
		
		if (topicSuffix.equals(TopicSuffixEnum.RESULT)) {
			try {
				applicationCache.setCommandResult(fullTopic, message);
			} catch (JsonProcessingException e) {
				throw new MessagingException("Failed to add message to cache.", e);
			}
		} else {
			if (topicSuffix.equals(TopicSuffixEnum.LWT)) {
				applicationCache.setConnectionState(topicHelper.getDeviceName(fullTopic), message);
			}
		}
		
		if (List.of(TopicSuffixEnum.LWT,TopicSuffixEnum.POWER, TopicSuffixEnum.SENSOR).contains(topicSuffix)) {
			var webSocketTopic = websocketTopicsPrefix + "/state-and-telemetry/" + fullTopic;
			webSocket.convertAndSend(webSocketTopic, message);
			LOGGER.info("Message [{}],{} in topic [{}] added to WebSocket topic [{}]", message, lineSeparator, fullTopic, webSocketTopic);
		}
	}
	private String addTimeStampToMessage(long timestamp, String message) throws JsonProcessingException {
		var jsonMessage = objectMapper.readTree(message);
		((ObjectNode) jsonMessage).put("timestamp", timestamp);
		return objectMapper.writeValueAsString(jsonMessage);
	}

	
	private String transformMessageToJson(String message, TopicSuffixEnum fieldName) {
		return "{\"" + fieldName +  "\":\"" + message + "\"}";
	}

	private TopicSuffixEnum getTopicSuffix(String topic) {
		var pattern = Pattern.compile("^(.+)/(.+)/(.+)$");
		var matcher = pattern.matcher(topic);
		if (! /* not */ matcher.matches()) {
			throw new IllegalArgumentException(String.format("Could not get suffix from %s", topic));
		}
		return TopicSuffixEnum.valueOf(matcher.group(3));
		
	}
}
