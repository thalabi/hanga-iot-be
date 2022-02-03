package com.kerneldc.hangariot.mqtt.service.handler;

import java.util.Date;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerneldc.hangariot.mqtt.message.LwtMessage;
import com.kerneldc.hangariot.mqtt.service.ApplicationCache;
import com.kerneldc.hangariot.mqtt.topic.TopicHelper;
import com.kerneldc.hangariot.mqtt.topic.TopicHelper.TopicSuffixEnum;


@Service
public class LwtMessageListenerHandler extends AbstractMessageListenerHandler {

	public LwtMessageListenerHandler(ApplicationCache applicationCache, ObjectMapper objectMapper,
			SimpMessagingTemplate webSocket, TopicHelper topicHelper) {
		super(applicationCache, objectMapper, webSocket, topicHelper);
	}

	@Override
	public boolean canHandleMessage(String fullTopic) {
		return getTopicSuffix(fullTopic).equals(TopicSuffixEnum.LWT);
	}

	@Override
	public void handleMessage(String fullTopic, long timestamp, String message) {
		
		var lwtMessage = new LwtMessage(message, new Date().getTime());
		// TODO
		//var stateMessage = new StateMessage(ConnectionStateEnum.valueOf(message.toUpperCase()), new Date().getTime());

		applicationCache.setConnectionState(topicHelper.getDeviceName(fullTopic), lwtMessage);
		// TODO
		//applicationCache.setConnectionState2(topicHelper.getDeviceName(fullTopic), stateMessage);

		publishMessageToWebSocket(fullTopic, lwtMessage);
		// TODO
		//publishMessageToWebSocket(// TODO fullTopic, stateMessage);
	}

}
