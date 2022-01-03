package com.kerneldc.hangariot.mqtt.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.kerneldc.hangariot.mqtt.topic.TopicEnum;
import com.kerneldc.hangariot.mqtt.topic.TopicHelper;
import com.kerneldc.hangariot.springconfig.MqttConfig.MessageSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SenderService {

	private final MessageSender messageSender;
	private final TopicHelper topicHelper;

	private static final String POWER_TOGGLER_COMMAND = "2";
	private static final String POWER_ON_COMMAND = "1";
	private static final String POWER_OFF_COMMAND = "0";
	private static final String POWER_STATE_COMMAND = StringUtils.EMPTY;

	private static final String TELEMETRY_PERIOD = "300"; // five minutes

	private enum Command {
		POWER_ON, POWER_OFF, TOGGLE_POWER, POWER_STATE, TRIGGER_SENSOR_DATA
	}

	public void powerOn(String device) {
		execute(device, Command.POWER_ON);
	}

	public void powerOff(String device) {
		execute(device, Command.POWER_OFF);
	}

	public void togglePower(String device) {
		execute(device, Command.TOGGLE_POWER);
	}

	public void getPowerState(String device) {
		execute(device, Command.POWER_STATE);
	}

	public void triggerSensorData(String device) {
		execute(device, Command.TRIGGER_SENSOR_DATA);
	}

	private void execute(String device, Command command) {

		switch (command) {
		case POWER_ON:
			sendMessage(topicHelper.getTopic(TopicEnum.COMMAND_POWER_TOPIC, device), POWER_ON_COMMAND);
			break;
		case POWER_OFF:
			sendMessage(topicHelper.getTopic(TopicEnum.COMMAND_POWER_TOPIC, device), POWER_OFF_COMMAND);
			break;
		case TOGGLE_POWER:
			sendMessage(topicHelper.getTopic(TopicEnum.COMMAND_POWER_TOPIC, device), POWER_TOGGLER_COMMAND);
			break;
		case POWER_STATE:
			sendMessage(topicHelper.getTopic(TopicEnum.COMMAND_POWER_TOPIC, device), POWER_STATE_COMMAND);
			break;
		case TRIGGER_SENSOR_DATA:
			sendMessage(topicHelper.getTopic(TopicEnum.COMMAND_TELEMETRY_PERIOD_TOPIC, device), TELEMETRY_PERIOD);
			break;
		}
	}

	private void sendMessage(String topic, String message) {
		LOGGER.info("Sending message [{}] to [{}] topic", message, topic);
		messageSender.sendMessage(topic, message);
	}
}
