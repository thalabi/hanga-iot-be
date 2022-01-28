package com.kerneldc.hangariot.mqtt.topic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
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
	

	@Value("${mqtt.topic.template.state-result}")
	private String stateResultTopicTemplate;
	
	@Value("${mqtt.topic.template.state-power}")
	private String statePowerTopicTemplate;

	
	@Value("${mqtt.topic.template.telemetry-sensor}")
	private String telemetrySensorTopicTemplate;

	@Value("${mqtt.topic.template.last-will-and-testament}")
	private String lastWillAndTestamentTopicTemplate;
	

	private final DeviceService deviceService;
	

	public String getCommandTopic(CommandEnum commandEnum, String deviceName) {
		return commandTopicTemplate.replace("<device>", deviceName)
				.replace("<command>", commandEnum.getCommand());
	}

	public String getLwtTopic(String deviceName) {
		return lastWillAndTestamentTopicTemplate.replace("<device>", deviceName);
	}


	public List<String> getTopicsToSubscribeTo() {
		var topicList = new ArrayList<String>();
		for (var device : deviceService.getDeviceNameList()) {
			var stateResultTopic = stateResultTopicTemplate.replace("<device>", device);
			var statePowerTopic = statePowerTopicTemplate.replace("<device>", device);
			var telemetrySensorTopic = telemetrySensorTopicTemplate.replace("<device>", device);
			var lastWillAndTestamentTopic = lastWillAndTestamentTopicTemplate.replace("<device>", device);
			topicList.add(stateResultTopic);
			topicList.add(statePowerTopic);
			topicList.add(telemetrySensorTopic);
			topicList.add(lastWillAndTestamentTopic);
			LOGGER.info("Device [{}], subscribed topics are [{}], [{}], [{}] & [{}]", device, stateResultTopic,
					statePowerTopic, telemetrySensorTopic, lastWillAndTestamentTopic);
		}
		return topicList;
	}

	public String getDeviceName(String topic) {
		var p = Pattern.compile("^.*/(.*)/.*$");
		var m = p.matcher(topic);
		if (m.matches() && StringUtils.isNotEmpty(m.group(1))) {
				return m.group(1);
		} else {
			return StringUtils.EMPTY;
		}
	}
}
