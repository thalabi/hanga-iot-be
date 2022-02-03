package com.kerneldc.hangariot.mqtt.result.timer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class TimersResultSerializer extends StdSerializer<TimersResult> {

	private static final long serialVersionUID = 1L;

	protected TimersResultSerializer() {
		this(null);
	}
	protected TimersResultSerializer(Class<TimersResult> t) {
		super(t);
	}

	@Override
	public void serialize(TimersResult value, JsonGenerator generator, SerializerProvider provider) throws IOException {
		generator.writeStartObject();
		generator.writeStringField("timers", value.getTimers());
		generator.writeObjectField("timer1", value.getTimerArray()[0]);
		generator.writeObjectField("timer2", value.getTimerArray()[1]);
		generator.writeObjectField("timer3", value.getTimerArray()[2]);
		generator.writeObjectField("timer4", value.getTimerArray()[3]);
		generator.writeObjectField("timer5", value.getTimerArray()[4]);
		generator.writeObjectField("timer6", value.getTimerArray()[5]);
		generator.writeObjectField("timer7", value.getTimerArray()[6]);
		generator.writeObjectField("timer8", value.getTimerArray()[7]);
		generator.writeObjectField("timer9", value.getTimerArray()[8]);
		generator.writeObjectField("timer10", value.getTimerArray()[9]);
		generator.writeObjectField("timer11", value.getTimerArray()[10]);
		generator.writeObjectField("timer12", value.getTimerArray()[11]);
		generator.writeObjectField("timer13", value.getTimerArray()[12]);
		generator.writeObjectField("timer14", value.getTimerArray()[13]);
		generator.writeObjectField("timer15", value.getTimerArray()[14]);
		generator.writeObjectField("timer16", value.getTimerArray()[15]);
		generator.writeObjectField("timestamp", value.getTimestamp());
		generator.writeEndObject();
	}
}
