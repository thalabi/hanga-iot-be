package com.kerneldc.hangariot.mqtt.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerneldc.hangariot.controller.TimeStdRequest;
import com.kerneldc.hangariot.controller.TimersRequest;
import com.kerneldc.hangariot.exception.ApplicationException;
import com.kerneldc.hangariot.mqtt.result.AbstractBaseResult;
import com.kerneldc.hangariot.mqtt.result.CommandEnum;
import com.kerneldc.hangariot.mqtt.result.PowerResult;
import com.kerneldc.hangariot.mqtt.result.TelePeriodResult;
import com.kerneldc.hangariot.mqtt.result.TimersResult;
import com.kerneldc.hangariot.mqtt.result.TimezoneResult;
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
	private final LastCommandResultCache lastCommandResultCache;
	private final ObjectMapper objectMapper;

	private static final String UNEXPECTED_RESULT_MESSAGE_FORMAT = "Executing [%s] command with argument [%s] failed. Result came back as [%s], expected [%s]";

	public void togglePower(String device, String powerStateExpected) throws InterruptedException, ApplicationException {
		var result = (PowerResult)executeCommand(device, CommandEnum.POWER, "2"); // 2 toggles power
		if (! /* not */ StringUtils.equalsIgnoreCase(powerStateExpected, result.getPower())) {
			throw new ApplicationException(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.POWER, "2", result.getPower(), powerStateExpected));
		}
	}

	public void triggerPowerState(String device) throws InterruptedException {
		executeCommand(device, CommandEnum.POWER);
	}

	public void triggerSensorData(String device) throws InterruptedException, ApplicationException {
		// issue the command without an argument to get the teleperiod value
		var result = (TelePeriodResult)executeCommand(device, CommandEnum.TELEPERIOD);
		// issue the command again with the retrieved argument to trigger an update on the SENSOR topic
		var result2 = (TelePeriodResult)executeCommand(device, CommandEnum.TELEPERIOD, String.valueOf(result.getTelePeriod()));
		if (! /* not */ result.getTelePeriod().equals(result2.getTelePeriod())) {
			throw new ApplicationException(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TELEPERIOD, result.getTelePeriod(), result2.getTelePeriod(), result.getTelePeriod()));
		}
	}


	public void triggerTimezoneValue(String device) throws InterruptedException {
		executeCommand(device, CommandEnum.TIMEZONE);
	}

	public void setTelePeriod(String device, String telePeriod) throws InterruptedException, ApplicationException {
		var result = (TelePeriodResult)executeCommand(device, CommandEnum.TELEPERIOD, telePeriod);
		if (! /* not */ result.getTelePeriod().equals(Integer.valueOf(telePeriod))) {
			throw new ApplicationException(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TELEPERIOD, telePeriod, result.getTelePeriod(), telePeriod));			
		}
	}

	public void setTimezoneOffset(String device, String timezoneOffset) throws InterruptedException, ApplicationException {
		var result = (TimezoneResult)executeCommand(device, CommandEnum.TIMEZONE, timezoneOffset);
		if (! /* not */ StringUtils.equals(timezoneOffset, "99") && ! /* not */ StringUtils.contains(timezoneOffset, ":")) {
			timezoneOffset += ":00";
		}
		// strip strings from colon and convert to integer for easier comparison  
		var resultTimezone = Integer.valueOf(result.getTimezone().replace(":", StringUtils.EMPTY));
		var expectedTimezone = Integer.valueOf(timezoneOffset.replace(":", StringUtils.EMPTY));
		if (! /* not */ resultTimezone.equals(expectedTimezone)) {
			throw new ApplicationException(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMEZONE, timezoneOffset, result.getTimezone(), timezoneOffset));			
		}
	}

	/*
	 * comment setTimers below has a timing issue
	 */
//	public void setTimers(TimersRequest timersRequest) throws JsonProcessingException, InterruptedException, ApplicationException {
//		// backlog has a limit of 30 commands and maximum of 698 characters
//		// split into three sets
//		var backlogArguments1 = new StringJoiner("; ");
//		backlogArguments1.add("timer1 " + objectMapper.writeValueAsString(timersRequest.getTimer1()));
//		backlogArguments1.add("timer2 " + objectMapper.writeValueAsString(timersRequest.getTimer2()));
//		backlogArguments1.add("timer3 " + objectMapper.writeValueAsString(timersRequest.getTimer3()));
//		backlogArguments1.add("timer4 " + objectMapper.writeValueAsString(timersRequest.getTimer4()));
//		backlogArguments1.add("timer5 " + objectMapper.writeValueAsString(timersRequest.getTimer5()));
//		LOGGER.info("backlog command arguments: [{}]", backlogArguments1.toString());
//		executeCommand(timersRequest.getDeviceName(), CommandEnum.BACKLOG, backlogArguments1.toString(), false);
//		
//		var backlogArguments2 = new StringJoiner("; ");
//		backlogArguments2.add("timer6 " + objectMapper.writeValueAsString(timersRequest.getTimer6()));
//		backlogArguments2.add("timer7 " + objectMapper.writeValueAsString(timersRequest.getTimer7()));
//		backlogArguments2.add("timer8 " + objectMapper.writeValueAsString(timersRequest.getTimer8()));
//		backlogArguments2.add("timer9 " + objectMapper.writeValueAsString(timersRequest.getTimer9()));
//		backlogArguments2.add("timer10 " + objectMapper.writeValueAsString(timersRequest.getTimer10()));
//		LOGGER.info("backlog command arguments: [{}]", backlogArguments2.toString());
//		executeCommand(timersRequest.getDeviceName(), CommandEnum.BACKLOG, backlogArguments2.toString(), false);
//		
//		var backlogArguments3 = new StringJoiner("; ");
//		backlogArguments3.add("timer11 " + objectMapper.writeValueAsString(timersRequest.getTimer11()));
//		backlogArguments3.add("timer12 " + objectMapper.writeValueAsString(timersRequest.getTimer12()));
//		backlogArguments3.add("timer13 " + objectMapper.writeValueAsString(timersRequest.getTimer13()));
//		backlogArguments3.add("timer14 " + objectMapper.writeValueAsString(timersRequest.getTimer14()));
//		backlogArguments3.add("timer15 " + objectMapper.writeValueAsString(timersRequest.getTimer15()));
//		backlogArguments3.add("timer16 " + objectMapper.writeValueAsString(timersRequest.getTimer16()));
//		LOGGER.info("backlog command arguments: [{}]", backlogArguments3.toString());
//		executeCommand(timersRequest.getDeviceName(), CommandEnum.BACKLOG, backlogArguments3.toString(), false);
//
//		executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMERS, timersRequest.getTimers());
//		
//		var result = (TimersResult)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMERS);
//		
//		if (! /* not */ StringUtils.equals(timersRequest.getTimers(), result.getTimers()) ||
//				! /* not */ timersRequest.getTimer1().equals(result.getTimer1()) ||
//				! /* not */ timersRequest.getTimer2().equals(result.getTimer2()) ||
//				! /* not */ timersRequest.getTimer3().equals(result.getTimer3()) ||
//				! /* not */ timersRequest.getTimer4().equals(result.getTimer4()) ||
//				! /* not */ timersRequest.getTimer5().equals(result.getTimer5()) ||
//				! /* not */ timersRequest.getTimer6().equals(result.getTimer6()) ||
//				! /* not */ timersRequest.getTimer7().equals(result.getTimer7()) ||
//				! /* not */ timersRequest.getTimer8().equals(result.getTimer8()) ||
//				! /* not */ timersRequest.getTimer9().equals(result.getTimer9()) ||
//				! /* not */ timersRequest.getTimer10().equals(result.getTimer10()) ||
//				! /* not */ timersRequest.getTimer11().equals(result.getTimer11()) ||
//				! /* not */ timersRequest.getTimer12().equals(result.getTimer12()) ||
//				! /* not */ timersRequest.getTimer13().equals(result.getTimer13()) ||
//				! /* not */ timersRequest.getTimer14().equals(result.getTimer14()) ||
//				! /* not */ timersRequest.getTimer15().equals(result.getTimer15()) ||
//				! /* not */ timersRequest.getTimer16().equals(result.getTimer16())
//				) {
//			throw new ApplicationException(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMERS, timersRequest, result, timersRequest));
//			}
//	}
	public void setTimers(TimersRequest timersRequest) throws JsonProcessingException, InterruptedException, ApplicationException {
		var applicationException = new ApplicationException();

		if (Boolean.TRUE.equals(timersRequest.getTimer1Modified())) {
			var timer1Result = (Timer1Result)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMER1, objectMapper.writeValueAsString(timersRequest.getTimer1()));
			if (! /* not */ timer1Result.getTimer1().equals(timersRequest.getTimer1())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMER1, timersRequest.getTimer1(), timer1Result.getTimer1(), timersRequest.getTimer1()));			
			}
		}
		if (Boolean.TRUE.equals(timersRequest.getTimer2Modified())) {
			var timer2Result = (Timer2Result)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMER2, objectMapper.writeValueAsString(timersRequest.getTimer2()));
			if (! /* not */ timer2Result.getTimer2().equals(timersRequest.getTimer2())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMER2, timersRequest.getTimer1(), timer2Result.getTimer2(), timersRequest.getTimer2()));			
			}
		}
		if (Boolean.TRUE.equals(timersRequest.getTimer3Modified())) {
			var timer3Result = (Timer3Result)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMER3, objectMapper.writeValueAsString(timersRequest.getTimer3()));
			if (! /* not */ timer3Result.getTimer3().equals(timersRequest.getTimer3())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMER3, timersRequest.getTimer3(), timer3Result.getTimer3(), timersRequest.getTimer3()));			
			}
		}
		if (Boolean.TRUE.equals(timersRequest.getTimer4Modified())) {
			var timer4Result = (Timer4Result)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMER4, objectMapper.writeValueAsString(timersRequest.getTimer4()));
			if (! /* not */ timer4Result.getTimer4().equals(timersRequest.getTimer4())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMER4, timersRequest.getTimer4(), timer4Result.getTimer4(), timersRequest.getTimer4()));			
			}
		}
		if (Boolean.TRUE.equals(timersRequest.getTimer5Modified())) {
			var timer5Result = (Timer5Result)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMER5, objectMapper.writeValueAsString(timersRequest.getTimer5()));
			if (! /* not */ timer5Result.getTimer5().equals(timersRequest.getTimer5())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMER5, timersRequest.getTimer5(), timer5Result.getTimer5(), timersRequest.getTimer5()));			
			}
		}
		if (Boolean.TRUE.equals(timersRequest.getTimer6Modified())) {
			var timer6Result = (Timer6Result)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMER6, objectMapper.writeValueAsString(timersRequest.getTimer6()));
			if (! /* not */ timer6Result.getTimer6().equals(timersRequest.getTimer6())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMER6, timersRequest.getTimer6(), timer6Result.getTimer6(), timersRequest.getTimer6()));			
			}
		}
		if (Boolean.TRUE.equals(timersRequest.getTimer7Modified())) {
			var timer7Result = (Timer7Result)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMER7, objectMapper.writeValueAsString(timersRequest.getTimer7()));
			if (! /* not */ timer7Result.getTimer7().equals(timersRequest.getTimer7())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMER7, timersRequest.getTimer7(), timer7Result.getTimer7(), timersRequest.getTimer7()));			
			}
		}
		if (Boolean.TRUE.equals(timersRequest.getTimer8Modified())) {
			var timer8Result = (Timer8Result)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMER8, objectMapper.writeValueAsString(timersRequest.getTimer8()));
			if (! /* not */ timer8Result.getTimer8().equals(timersRequest.getTimer8())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMER8, timersRequest.getTimer8(), timer8Result.getTimer8(), timersRequest.getTimer8()));			
			}
		}
		if (Boolean.TRUE.equals(timersRequest.getTimer9Modified())) {
			var timer9Result = (Timer9Result)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMER9, objectMapper.writeValueAsString(timersRequest.getTimer9()));
			if (! /* not */ timer9Result.getTimer9().equals(timersRequest.getTimer9())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMER9, timersRequest.getTimer9(), timer9Result.getTimer9(), timersRequest.getTimer9()));			
			}
		}
		if (Boolean.TRUE.equals(timersRequest.getTimer10Modified())) {
			var timer10Result = (Timer10Result)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMER10, objectMapper.writeValueAsString(timersRequest.getTimer10()));
			if (! /* not */ timer10Result.getTimer10().equals(timersRequest.getTimer10())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMER10, timersRequest.getTimer10(), timer10Result.getTimer10(), timersRequest.getTimer10()));			
			}
		}
		if (Boolean.TRUE.equals(timersRequest.getTimer11Modified())) {
			var timer11Result = (Timer11Result)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMER11, objectMapper.writeValueAsString(timersRequest.getTimer11()));
			if (! /* not */ timer11Result.getTimer11().equals(timersRequest.getTimer11())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMER11, timersRequest.getTimer11(), timer11Result.getTimer11(), timersRequest.getTimer11()));			
			}
		}
		if (Boolean.TRUE.equals(timersRequest.getTimer12Modified())) {
			var timer12Result = (Timer12Result)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMER12, objectMapper.writeValueAsString(timersRequest.getTimer12()));
			if (! /* not */ timer12Result.getTimer12().equals(timersRequest.getTimer12())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMER12, timersRequest.getTimer12(), timer12Result.getTimer12(), timersRequest.getTimer12()));			
			}
		}
		if (Boolean.TRUE.equals(timersRequest.getTimer13Modified())) {
			var timer13Result = (Timer13Result)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMER13, objectMapper.writeValueAsString(timersRequest.getTimer13()));
			if (! /* not */ timer13Result.getTimer13().equals(timersRequest.getTimer13())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMER13, timersRequest.getTimer13(), timer13Result.getTimer13(), timersRequest.getTimer13()));			
			}
		}
		if (Boolean.TRUE.equals(timersRequest.getTimer14Modified())) {
			var timer14Result = (Timer14Result)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMER14, objectMapper.writeValueAsString(timersRequest.getTimer14()));
			if (! /* not */ timer14Result.getTimer14().equals(timersRequest.getTimer14())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMER14, timersRequest.getTimer14(), timer14Result.getTimer14(), timersRequest.getTimer14()));			
			}
		}
		if (Boolean.TRUE.equals(timersRequest.getTimer15Modified())) {
			var timer15Result = (Timer15Result)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMER15, objectMapper.writeValueAsString(timersRequest.getTimer15()));
			if (! /* not */ timer15Result.getTimer15().equals(timersRequest.getTimer15())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMER15, timersRequest.getTimer15(), timer15Result.getTimer15(), timersRequest.getTimer15()));			
			}
		}
		if (Boolean.TRUE.equals(timersRequest.getTimer16Modified())) {
			var timer16Result = (Timer16Result)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMER16, objectMapper.writeValueAsString(timersRequest.getTimer16()));
			if (! /* not */ timer16Result.getTimer16().equals(timersRequest.getTimer16())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMER16, timersRequest.getTimer16(), timer16Result.getTimer16(), timersRequest.getTimer16()));			
			}
		}


		if (Boolean.TRUE.equals(timersRequest.getTimersModified())) {
			var timersResult = (TimersResult)executeCommand(timersRequest.getDeviceName(), CommandEnum.TIMERS,timersRequest.getTimers());
			if (! /* not */ StringUtils.equals(timersRequest.getTimers(), timersResult.getTimers())) {
				applicationException.addMessage(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TIMERS, timersRequest, timersResult, timersRequest));
			}
		}
		
		if (! /* not */ CollectionUtils.isEmpty(applicationException.getMessageList())) {
			throw applicationException;
		}
	}
	
	public TimersResult getTimers(String device) throws InterruptedException {
		return (TimersResult)executeCommand(device, CommandEnum.TIMERS);

	}

	public void setTimeStdt(String deviceName, TimeStdRequest timeStdRequest) {
		// TODO Auto-generated method stub
		
	}
	
	
	private AbstractBaseResult executeCommand(String device, CommandEnum commandEnum) throws InterruptedException {
		return executeCommand(device, commandEnum, StringUtils.EMPTY, true);
		
	}
	public AbstractBaseResult executeCommand(String device, CommandEnum commandEnum, String stringArgument) throws InterruptedException {
		return executeCommand(device, commandEnum, stringArgument, true);
	}
	
	private synchronized AbstractBaseResult executeCommand(String device, CommandEnum commandEnum, String stringArgument, boolean wait) throws InterruptedException {
		if (wait) {
			var commandTimestamp = new Date().getTime();
			sendMessage(topicHelper.getCommandTopic(commandEnum, device), stringArgument);
			return waitForCommandToExecute(device, commandEnum, commandTimestamp); 
		} else {
			sendMessage(topicHelper.getCommandTopic(commandEnum, device), stringArgument);
			return null;
		}
	}

	private void sendMessage(String topic, String message) {
		LOGGER.info("Sending message [{}] to [{}] topic", message, topic);
		messageSender.sendMessage(topic, message);
	}

	private static final int MAX_NUMBER_OF_TRIES = 50;
    public AbstractBaseResult waitForCommandToExecute(String deviceName, CommandEnum commandEnum, long commandTimestamp) throws InterruptedException {
    	AbstractBaseResult result;
    	int count = 0;
		do {
			LOGGER.info("Waiting 100 ms for command to finish execution ...");
			TimeUnit.MILLISECONDS.sleep(100);
			count++;
			result = lastCommandResultCache.getCommandResult(deviceName, commandEnum);
		} while (result == null && count <= MAX_NUMBER_OF_TRIES || result != null && result.getTimestamp() <= commandTimestamp && count <= MAX_NUMBER_OF_TRIES);
		return result;
    }


}
