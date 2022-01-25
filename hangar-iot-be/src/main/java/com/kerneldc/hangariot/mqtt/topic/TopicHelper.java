package com.kerneldc.hangariot.mqtt.topic;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kerneldc.hangariot.mqtt.result.CommandEnum;
import com.kerneldc.hangariot.mqtt.service.DeviceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TopicHelper {

	@Value("${mqtt.topic.template.command}")
	private String commandTopicTemplate;
	
//	@Value("${mqtt.topic.template.command-power}")
//	private String commandPowerTopicTemplate;
//	
//	@Value("${mqtt.topic.template.command-telemetry-period}")
//	private String commandTelemetryPeriodTopicTemplate;
//	
//	@Value("${mqtt.topic.template.command-timezone}")
//	private String commandTimezoneTemplate;

	
	@Value("${mqtt.topic.template.state-result}")
	private String stateResultTopicTemplate;
	
	@Value("${mqtt.topic.template.state-power}")
	private String statePowerTopicTemplate;

	@Value("${mqtt.topic.template.telemetry-sensor}")
	private String telemetrySensorTopicTemplate;
	
	private final DeviceService deviceService;
	

	public String getCommandTopic(CommandEnum commandEnum, String device) {
		return commandTopicTemplate.replace("<device>", device)
				.replace("<command>", commandEnum.getCommand());
	}

	public List<String> getTopicsToSubscribeTo() {
		var topicList = new ArrayList<String>();
		for (var device : deviceService.getDeviceNameList()) {
			var stateResultTopic = stateResultTopicTemplate.replace("<device>", device);
			var statePowerTopic = statePowerTopicTemplate.replace("<device>", device);
			var telemetrySensorTopic = telemetrySensorTopicTemplate.replace("<device>", device);
			topicList.add(stateResultTopic);
			topicList.add(statePowerTopic);
			topicList.add(telemetrySensorTopic);
			LOGGER.info("Device [{}], subscribed topics are [{}], [{}] & [{}]", device, stateResultTopic,
					statePowerTopic, telemetrySensorTopic);
		}
		return topicList;
	}

}
