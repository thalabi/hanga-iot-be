package com.kerneldc.hangariot.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.kerneldc.hangariot.mqtt.command.Timer;

@Component
public class TimersRequestDeserializer extends StdDeserializer<TimersRequest> {

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private ObjectMapper objectMapper;

	protected TimersRequestDeserializer() {
		this(null);
	}

	protected TimersRequestDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public TimersRequest deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		var timersRequest = new TimersRequest();
		JsonNode node = jp.getCodec().readTree(jp);
		
		
		timersRequest.setTimers(node.get("timers").asText());
		timersRequest.getTimerArray()[0] = objectMapper.treeToValue(node.get("timer1"), Timer.class);
		timersRequest.getTimerArray()[1] = objectMapper.treeToValue(node.get("timer2"), Timer.class);
		timersRequest.getTimerArray()[2] = objectMapper.treeToValue(node.get("timer3"), Timer.class);
		timersRequest.getTimerArray()[3] = objectMapper.treeToValue(node.get("timer4"), Timer.class);
		timersRequest.getTimerArray()[4] = objectMapper.treeToValue(node.get("timer5"), Timer.class);
		timersRequest.getTimerArray()[5] = objectMapper.treeToValue(node.get("timer6"), Timer.class);
		timersRequest.getTimerArray()[6] = objectMapper.treeToValue(node.get("timer7"), Timer.class);
		timersRequest.getTimerArray()[7] = objectMapper.treeToValue(node.get("timer8"), Timer.class);
		timersRequest.getTimerArray()[8] = objectMapper.treeToValue(node.get("timer9"), Timer.class);
		timersRequest.getTimerArray()[9] = objectMapper.treeToValue(node.get("timer10"), Timer.class);
		timersRequest.getTimerArray()[10] = objectMapper.treeToValue(node.get("timer11"), Timer.class);
		timersRequest.getTimerArray()[11] = objectMapper.treeToValue(node.get("timer12"), Timer.class);
		timersRequest.getTimerArray()[12] = objectMapper.treeToValue(node.get("timer13"), Timer.class);
		timersRequest.getTimerArray()[13] = objectMapper.treeToValue(node.get("timer14"), Timer.class);
		timersRequest.getTimerArray()[14] = objectMapper.treeToValue(node.get("timer15"), Timer.class);
		timersRequest.getTimerArray()[15] = objectMapper.treeToValue(node.get("timer16"), Timer.class);

		timersRequest.setDeviceName(node.get("deviceName").asText());
		timersRequest.setTimersModified(node.get("timersModified").asBoolean());
		
		timersRequest.getTimerModifiedArray()[0] = node.get("timer1Modified").asBoolean();
		timersRequest.getTimerModifiedArray()[1] = node.get("timer2Modified").asBoolean();
		timersRequest.getTimerModifiedArray()[2] = node.get("timer3Modified").asBoolean();
		timersRequest.getTimerModifiedArray()[3] = node.get("timer4Modified").asBoolean();
		timersRequest.getTimerModifiedArray()[4] = node.get("timer5Modified").asBoolean();
		timersRequest.getTimerModifiedArray()[5] = node.get("timer6Modified").asBoolean();
		timersRequest.getTimerModifiedArray()[6] = node.get("timer7Modified").asBoolean();
		timersRequest.getTimerModifiedArray()[7] = node.get("timer8Modified").asBoolean();
		timersRequest.getTimerModifiedArray()[8] = node.get("timer9Modified").asBoolean();
		timersRequest.getTimerModifiedArray()[9] = node.get("timer10Modified").asBoolean();
		timersRequest.getTimerModifiedArray()[10] = node.get("timer11Modified").asBoolean();
		timersRequest.getTimerModifiedArray()[11] = node.get("timer12Modified").asBoolean();
		timersRequest.getTimerModifiedArray()[12] = node.get("timer13Modified").asBoolean();
		timersRequest.getTimerModifiedArray()[13] = node.get("timer14Modified").asBoolean();
		timersRequest.getTimerModifiedArray()[14] = node.get("timer15Modified").asBoolean();
		timersRequest.getTimerModifiedArray()[15] = node.get("timer16Modified").asBoolean();
		timersRequest.setTimestamp(node.get("timestamp").longValue());

		return timersRequest;
	}

}
