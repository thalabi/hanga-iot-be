package com.kerneldc.hangariot.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kerneldc.hangariot.exception.ApplicationException;
import com.kerneldc.hangariot.mqtt.result.AbstractBaseResult;
import com.kerneldc.hangariot.mqtt.result.CommandEnum;
import com.kerneldc.hangariot.mqtt.result.TimersResult;
import com.kerneldc.hangariot.mqtt.service.DeviceService;
import com.kerneldc.hangariot.mqtt.service.LastCommandResultCache;
import com.kerneldc.hangariot.mqtt.service.SenderService;
import com.kerneldc.hangariot.task.ScheduledTasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("hangarIotController")
@RequiredArgsConstructor
@Slf4j
public class HangarIotController {

	private final SenderService senderService;
	private final DeviceService deviceService;
	private final LastCommandResultCache lastCommandResultCache;
	private final ScheduledTasks scheduledTasks;

    @GetMapping("/ping")
	public ResponseEntity<PingResponse> ping() {
    	LOGGER.info("Begin ...");
    	PingResponse pingResponse = new PingResponse();
    	pingResponse.setMessage("pong");
    	pingResponse.setTimestamp(LocalDateTime.now());
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(pingResponse);
    }
    
    @PostMapping("/togglePower")
	public ResponseEntity<String> togglePower(@Valid @RequestBody TogglePowerRequest togglePowerRequest) throws InterruptedException {
    	LOGGER.info("Begin ...");
    	if (! /* not */ validateDeviceName(togglePowerRequest.getDeviceName())) {
    		return ResponseEntity.badRequest().body("Invalid device name");
    	}
    	try {
			senderService.togglePower(togglePowerRequest.getDeviceName(), togglePowerRequest.getPowerStateRequested());
		} catch (ApplicationException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(NestedExceptionUtils.getMostSpecificCause(e).getMessage());
		}
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(null);
    }

    /*
     * Should be used by devices that support sensor data like the Sonoff S-31 
     */
    @PostMapping("/triggerSensorData")
	public ResponseEntity<String> triggerSensorData(@Valid @RequestBody DeviceRequest deviceRequest) throws InterruptedException {
    	LOGGER.info("Begin ...");
    	if (! /* not */ validateDeviceName(deviceRequest.getDeviceName())) {
    		return ResponseEntity.badRequest().body("Invalid device name");
    	}
    	try {
			senderService.triggerSensorData(deviceRequest.getDeviceName());
		} catch (ApplicationException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(NestedExceptionUtils.getMostSpecificCause(e).getMessage());
		}
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(null);
    }
    
    @PostMapping("/triggerPowerState")
	public ResponseEntity<String> triggerPowerState(@Valid @RequestBody DeviceRequest deviceRequest) throws InterruptedException {
    	LOGGER.info("Begin ...");
    	if (! /* not */ validateDeviceName(deviceRequest.getDeviceName())) {
    		LOGGER.error("Device [{}] not found.", deviceRequest.getDeviceName());
    		return ResponseEntity.badRequest().body("Invalid device name");
    	}
    	senderService.triggerPowerState(deviceRequest.getDeviceName());
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(null);
    }
    
    @PostMapping("/triggerTimezoneValue")
	public ResponseEntity<String> triggerTimezoneValue(@Valid @RequestBody DeviceRequest deviceRequest) throws InterruptedException {
    	LOGGER.info("Begin ...");
    	if (! /* not */ validateDeviceName(deviceRequest.getDeviceName())) {
    		return ResponseEntity.badRequest().body("Invalid device name");
    	}
		senderService.triggerTimezoneValue(deviceRequest.getDeviceName());
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(null);
    }
    
    @PostMapping("/setTelePeriod")
	public ResponseEntity<String> setTelePeriod(@Valid @RequestBody TelePeriodRequest timezoneRequest) throws InterruptedException {
    	LOGGER.info("Begin ...");
    	var deviceName = timezoneRequest.getDeviceName();
    	if (! /* not */ validateDeviceName(deviceName)) {
    		return ResponseEntity.badRequest().body("Invalid device name");
    	}
    	try {
			senderService.setTelePeriod(deviceName, timezoneRequest.getTelePeriod());
		} catch (ApplicationException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(NestedExceptionUtils.getMostSpecificCause(e).getMessage());
		}
    	
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(null);
    }
    
    @PostMapping("/setTimezoneOffset")
	public ResponseEntity<String> setTimezoneOffset(@Valid @RequestBody TimezoneRequest timezoneRequest) throws InterruptedException {
    	LOGGER.info("Begin ...");
    	var deviceName = timezoneRequest.getDeviceName();
    	if (! /* not */ validateDeviceName(deviceName)) {
    		return ResponseEntity.badRequest().body("Invalid device name");
    	}
    	try {
			senderService.setTimezoneOffset(deviceName, timezoneRequest.getTimezoneOffset());
		} catch (ApplicationException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(NestedExceptionUtils.getMostSpecificCause(e).getMessage());
		}
    	
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(null);
    }
    
    // TODO not fully coded
    @PostMapping("/setTimeStd")
	public ResponseEntity<String> setsetTimeStd(@Valid @RequestBody TimeStdRequest timeStdRequest) throws InterruptedException {
    	LOGGER.info("Begin ...");
    	var deviceName = timeStdRequest.getDeviceName();
    	if (! /* not */ validateDeviceName(deviceName)) {
    		return ResponseEntity.badRequest().body("Invalid device name");
    	}
//    	try {
			senderService.setTimeStdt(deviceName, timeStdRequest);
//		} catch (ApplicationException e) {
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(NestedExceptionUtils.getMostSpecificCause(e).getMessage());
//		}
    	
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(null);
    }
    
    @GetMapping("/getTimers")
    public ResponseEntity<TimersResult> getTimers(@Valid String deviceName) throws JsonProcessingException, InterruptedException {
    	LOGGER.info("Begin ...");
    	if (! /* not */ validateDeviceName(deviceName)) {
    		return ResponseEntity.badRequest().body(null);
    	}
    	
    	var result = senderService.getTimers(deviceName);
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(result);
    }

    @PostMapping("/setTimers")
	public ResponseEntity<String> setTimers(@Valid @RequestBody TimersRequest timersRequest) throws InterruptedException, JsonProcessingException {
    	LOGGER.info("Begin ...");
    	var deviceName = timersRequest.getDeviceName();
    	if (! /* not */ validateDeviceName(deviceName)) {
    		return ResponseEntity.badRequest().body("Invalid device name");
    	}
    	try {
			senderService.setTimers(timersRequest);
		} catch (ApplicationException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(NestedExceptionUtils.getMostSpecificCause(e).getMessage());
		}
    	
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(null);
    }

    @PostMapping("/executeFreeFormatCommand")
	public ResponseEntity<AbstractBaseResult> executeFreeFormatCommand(@Valid @RequestBody FreeFormatCommandRequest freeFormatCommandRequest) throws InterruptedException {
    	LOGGER.info("Begin ...");
    	var deviceName = freeFormatCommandRequest.getDeviceName();
    	if (! /* not */ validateDeviceName(deviceName)) {
    		return ResponseEntity.badRequest().body(null);
    	}
    	var commandEnum = Enum.valueOf(CommandEnum.class, freeFormatCommandRequest.getCommand().toUpperCase());
		var abstractBaseResult = senderService.executeCommand(freeFormatCommandRequest.getDeviceName(), commandEnum, freeFormatCommandRequest.getArguments());
		LOGGER.info("abstractBaseResult: [{}]", abstractBaseResult);
		var result = commandEnum.getResultType().cast(abstractBaseResult);
		
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(result);
    }
    
    @GetMapping("/getDeviceList")
	public ResponseEntity<List<DeviceResponse>> getDeviceList() {
    	LOGGER.info("Begin ...");
    	var deviceResponseList = deviceService.getDeviceList().stream().map(device -> {
    		var deviceResponse = new DeviceResponse();
    		deviceResponse.setDevice(device);
    		return deviceResponse;
    	}).toList();
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(deviceResponseList);
    }
    
    @GetMapping("/getCommandList")
	public ResponseEntity<List<CommandResponse>> getCommandList() {
    	LOGGER.info("Begin ...");
    	var commandList = deviceService.getCommandList().stream().map(command -> {
    		var commandResponse = new CommandResponse();
    		commandResponse.setCommand(command);
    		return commandResponse;
    	}).toList();
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(commandList);
    }
    
    @GetMapping("/dumpCache")
	public ResponseEntity<Void> dumpCache() {
    	LOGGER.info("Begin ...");
    	lastCommandResultCache.dumpCache();
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(null);
    }
    
    @PostMapping("/increaseTelemetryPeriod")
	public ResponseEntity<Void> increaseTelemetryPeriod() throws InterruptedException {
    	LOGGER.info("Begin ...");
    	scheduledTasks.increaseTelemetryPeriod();
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(null);
    }

    private boolean validateDeviceName(String deviceName) {
    	return deviceService.getDeviceNameList().contains(deviceName);
    }
}
