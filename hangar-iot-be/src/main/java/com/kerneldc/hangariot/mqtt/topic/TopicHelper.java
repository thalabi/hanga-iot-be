package com.kerneldc.hangariot.mqtt.topic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kerneldc.hangariot.mqtt.service.DeviceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TopicHelper {

	@Value("${mqtt.topic.template.command-power}")
	private String commandPowerTopicTemplate;
	
	@Value("${mqtt.topic.template.command-telemetry-period}")
	private String commandTelemetryPeriodTopicTemplate;

	
	@Value("${mqtt.topic.template.state-result}")
	private String stateResultTopicTemplate;
	
	@Value("${mqtt.topic.template.state-power}")
	private String statePowerTopicTemplate;

	@Value("${mqtt.topic.template.telemetry-sensor}")
	private String telemetrySensorTopicTemplate;
	
	private final DeviceService deviceService;
	
	public String getTopic(TopicEnum topicEnum, String device) {
		var topicTemplate = StringUtils.EMPTY;
		switch (topicEnum) {
		case COMMAND_POWER_TOPIC:
			topicTemplate = commandPowerTopicTemplate;
			break;
		case COMMAND_TELEMETRY_PERIOD_TOPIC:
			topicTemplate = commandTelemetryPeriodTopicTemplate;
			break;
		case STATE_RESULT_TOPIC:
			topicTemplate = stateResultTopicTemplate;
			break;
		case STATE_POWER_TOPIC:
			topicTemplate = statePowerTopicTemplate;
			break;
		case TELEMETRY_SENSOR_TOPIC:
			topicTemplate = telemetrySensorTopicTemplate;
			break;
		}
		return topicTemplate.replace("<device>", device);
	}

	public List<String> getTopicsToSubscribeTo() {
		var topicList = new ArrayList<String>();
		for (var device : deviceService.getDeviceNameList()) {
			var stateResultTopic = getTopic(TopicEnum.STATE_RESULT_TOPIC, device);
			var statePowerTopic = getTopic(TopicEnum.STATE_POWER_TOPIC, device);
			var telemetrySensorTopic = getTopic(TopicEnum.TELEMETRY_SENSOR_TOPIC, device);
			topicList.add(stateResultTopic);
			topicList.add(statePowerTopic);
			topicList.add(telemetrySensorTopic);
			LOGGER.info("Device [{}], subscribed topics are [{}], [{}] & [{}]", device, stateResultTopic,
					statePowerTopic, telemetrySensorTopic);
		}
		return topicList;
	}

}
