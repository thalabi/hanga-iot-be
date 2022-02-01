package com.kerneldc.hangariot.mqtt.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerneldc.hangariot.controller.TimeStdRequest;
import com.kerneldc.hangariot.controller.TimersRequest;
import com.kerneldc.hangariot.exception.ApplicationException;
import com.kerneldc.hangariot.exception.DeviceOfflineException;
import com.kerneldc.hangariot.mqtt.message.LwtMessage;
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

	// MQTT messages
	private final MessageSender messageSender;
	// WebSocket messages
	private final SimpMessagingTemplate webSocket;
	private final TopicHelper topicHelper;
	private final ApplicationCache applicationCache;
	private final ObjectMapper objectMapper;

	@Value("${websocket.topics.prefix:/topic}")
	private String websocketTopicsPrefix;

	private static final String UNEXPECTED_RESULT_MESSAGE_FORMAT = "Executing [%s] command with argument [%s] failed. Result came back as [%s], expected [%s]";

	public void togglePower(String device, String powerStateExpected) throws InterruptedException, ApplicationException, DeviceOfflineException {
		var result = (PowerResult)executeCommand(device, CommandEnum.POWER, "2"); // 2 toggles power
		if (! /* not */ StringUtils.equalsIgnoreCase(powerStateExpected, result.getPower())) {
			throw new ApplicationException(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.POWER, "2", result.getPower(), powerStateExpected));
		}
	}

	public void triggerPublishPowerState(String device) throws InterruptedException, DeviceOfflineException {
		executeCommand(device, CommandEnum.POWER);
	}

	public void triggerPublishSensorData(String device) throws InterruptedException, ApplicationException, DeviceOfflineException {
		checkDeviceOnline(device);
		// issue the command without an argument to get the teleperiod value
		var result = (TelePeriodResult)executeCommand(device, CommandEnum.TELEPERIOD);
		// issue the command again with the retrieved argument to trigger an update on the SENSOR topic
		var result2 = (TelePeriodResult)executeCommand(device, CommandEnum.TELEPERIOD, String.valueOf(result.getTelePeriod()));
		if (! /* not */ result.getTelePeriod().equals(result2.getTelePeriod())) {
			throw new ApplicationException(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TELEPERIOD, result.getTelePeriod(), result2.getTelePeriod(), result.getTelePeriod()));
		}
	}


	private void checkDeviceOnline(String deviceName) throws DeviceOfflineException {
		if (! /* not */ applicationCache.isDeviceOnLine(deviceName)) {
			throw new DeviceOfflineException();
		}
	}

	public void triggerTimezoneValue(String device) throws InterruptedException, DeviceOfflineException {
		executeCommand(device, CommandEnum.TIMEZONE);
	}

	public void setTelePeriod(String device, String telePeriod) throws InterruptedException, ApplicationException, DeviceOfflineException {
		var result = (TelePeriodResult)executeCommand(device, CommandEnum.TELEPERIOD, telePeriod);
		if (! /* not */ applicationCache.isDeviceOnLine(device)) {
			return;
		}
		if (! /* not */ result.getTelePeriod().equals(Integer.valueOf(telePeriod))) {
			throw new ApplicationException(String.format(UNEXPECTED_RESULT_MESSAGE_FORMAT, CommandEnum.TELEPERIOD, telePeriod, result.getTelePeriod(), telePeriod));			
		}
	}

	public void setTimezoneOffset(String device, String timezoneOffset) throws InterruptedException, ApplicationException, DeviceOfflineException {
		var result = (TimezoneResult)executeCommand(device, CommandEnum.TIMEZONE, timezoneOffset);
		if (! /* not */ applicationCache.isDeviceOnLine(device)) {
			return;
		}
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

	public void setTimers(TimersRequest timersRequest) throws JsonProcessingException, InterruptedException, ApplicationException, DeviceOfflineException {
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
	
	public TimersResult getTimers(String device) throws InterruptedException, DeviceOfflineException {
		return (TimersResult)executeCommand(device, CommandEnum.TIMERS);

	}

	public void setTimeStdt(String deviceName, TimeStdRequest timeStdRequest) {
		// TODO Auto-generated method stub
		
	}
	
	
	private AbstractBaseResult executeCommand(String device, CommandEnum commandEnum) throws InterruptedException, DeviceOfflineException {
		return executeCommand(device, commandEnum, StringUtils.EMPTY, true);
		
	}
	public AbstractBaseResult executeCommand(String device, CommandEnum commandEnum, String stringArgument) throws InterruptedException, DeviceOfflineException {
		return executeCommand(device, commandEnum, stringArgument, true);
	}
	
	private synchronized AbstractBaseResult executeCommand(String device, CommandEnum commandEnum, String stringArgument, boolean wait) throws InterruptedException, DeviceOfflineException {
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
	private static final int SLEEP_MILLISECONDS = 100;
	public AbstractBaseResult waitForCommandToExecute(String deviceName, CommandEnum commandEnum, long commandTimestamp) throws InterruptedException, DeviceOfflineException {
    	AbstractBaseResult result;
    	int count = 0;
    	LOGGER.info("Waiting for command to finish execution ...");
		do {
			TimeUnit.MILLISECONDS.sleep(SLEEP_MILLISECONDS);
			count++;
			result = applicationCache.getCommandResult(deviceName, commandEnum);
		} while (result == null && count < MAX_NUMBER_OF_TRIES || result != null && result.getTimestamp() <= commandTimestamp && count < MAX_NUMBER_OF_TRIES);
		LOGGER.info("Waited [{}] seconds", count * SLEEP_MILLISECONDS / 1000f);
		
		if (count == MAX_NUMBER_OF_TRIES) {
			LOGGER.warn("Timed out waiting for command [{}] to execute on device [{}]", commandEnum, deviceName);
			LOGGER.warn("Marking device [{}] as Offline", deviceName);
			var lwtMessage = new LwtMessage("Offline", new Date().getTime());
			applicationCache.setConnectionState(deviceName, lwtMessage);
			triggerPublishConnectionState(deviceName);
			throw new DeviceOfflineException();
		}
		return result;
    }


    public void triggerPublishConnectionState(String deviceName) {
    	LOGGER.info("Publishing LwtMessage message [{}] of device [{}]", applicationCache.getConnectionState(deviceName), deviceName);
    	var webSocketTopic = websocketTopicsPrefix + "/state-and-telemetry/" + topicHelper.getLwtTopic(deviceName);
    	webSocket.convertAndSend(webSocketTopic, applicationCache.getConnectionState(deviceName));
    }

}
