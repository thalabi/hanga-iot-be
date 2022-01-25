package com.kerneldc.hangariot.mqtt.result;

import com.kerneldc.hangariot.mqtt.result.timer.Timer10Result;
import com.kerneldc.hangariot.mqtt.result.timer.Timer11Result;
import com.kerneldc.hangariot.mqtt.result.timer.Timer12Result;
import com.kerneldc.hangariot.mqtt.result.timer.Timer13Result;
import com.kerneldc.hangariot.mqtt.result.timer.Timer14Result;
import com.kerneldc.hangariot.mqtt.result.timer.Timer15Result;
import com.kerneldc.hangariot.mqtt.result.timer.Timer16Result;
import com.kerneldc.hangariot.mqtt.result.timer.Timer1Result;
import com.kerneldc.hangariot.mqtt.result.timer.Timer2Result;
import com.kerneldc.hangariot.mqtt.result.timer.Timer3Result;
import com.kerneldc.hangariot.mqtt.result.timer.Timer4Result;
import com.kerneldc.hangariot.mqtt.result.timer.Timer5Result;
import com.kerneldc.hangariot.mqtt.result.timer.Timer6Result;
import com.kerneldc.hangariot.mqtt.result.timer.Timer7Result;
import com.kerneldc.hangariot.mqtt.result.timer.Timer8Result;
import com.kerneldc.hangariot.mqtt.result.timer.Timer9Result;

public enum CommandEnum {
	POWER("power", PowerResult.class),
	TIMEZONE("timezone", TimezoneResult.class),
	TIMEDST("timedst", TimeDstResult.class),
	TIMESTD("timestd", TimeStdResult.class),
	TELEPERIOD("teleperiod", TelePeriodResult.class),
	
	TIMERS("timers", TimersResult.class),
	TIMER1("timer1", Timer1Result.class),
	TIMER2("timer2", Timer2Result.class),
	TIMER3("timer3", Timer3Result.class),
	TIMER4("timer4", Timer4Result.class),
	TIMER5("timer5", Timer5Result.class),
	TIMER6("timer6", Timer6Result.class),
	TIMER7("timer7", Timer7Result.class),
	TIMER8("timer8", Timer8Result.class),
	TIMER9("timer9", Timer9Result.class),
	TIMER10("timer10", Timer10Result.class),
	TIMER11("timer11", Timer11Result.class),
	TIMER12("timer12", Timer12Result.class),
	TIMER13("timer13", Timer13Result.class),
	TIMER14("timer14", Timer14Result.class),
	TIMER15("timer15", Timer15Result.class),
	TIMER16("timer16", Timer16Result.class),
	BACKLOG("backlog", null);
	
	String command;
	Class<? extends AbstractBaseResult> resultType;
	
	CommandEnum(String command, Class<? extends AbstractBaseResult> resultType) {
		this.command = command;
		this.resultType = resultType;
	}

	public String getCommand() {
		return command;
	}

	public Class<? extends AbstractBaseResult> getResultType() {
		return resultType;
	}

}
