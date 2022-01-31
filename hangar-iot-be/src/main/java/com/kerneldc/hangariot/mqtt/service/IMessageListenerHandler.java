package com.kerneldc.hangariot.mqtt.service;

public interface IMessageListenerHandler {

	boolean canHandleMessage(String fullTopic);
	void handleMessage(String fullTopic, long timestamp, String message);
}
