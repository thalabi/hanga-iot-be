package com.kerneldc.hangariot.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kerneldc.hangariot.mqtt.service.DeviceResponse;
import com.kerneldc.hangariot.mqtt.service.DeviceService;
import com.kerneldc.hangariot.mqtt.service.SenderService;
import com.kerneldc.hangariot.mqtt.service.StateAndTelemetryCache;
import com.kerneldc.hangariot.mqtt.topic.TopicEnum;
import com.kerneldc.hangariot.mqtt.topic.TopicHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("hangarIotController")
@RequiredArgsConstructor
@Slf4j
public class HangarIotController {

	private final SenderService senderService;
	private final StateAndTelemetryCache stateAndTelemetryCache;
	private final TopicHelper topicHelper;
	private final DeviceService deviceService;
	
    @GetMapping("/ping")
	public ResponseEntity<PingResponse> ping() {
    	LOGGER.info("Begin ...");
    	PingResponse pingResponse = new PingResponse();
    	pingResponse.setMessage("pong");
    	pingResponse.setTimestamp(LocalDateTime.now());
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(pingResponse);
    }

    @PostMapping("/powerOn")
	public ResponseEntity<Void> powerOn(@Valid @RequestBody DeviceRequest deviceRequest) {
    	LOGGER.info("Begin ...");
    	senderService.powerOn(deviceRequest.getDeviceName());
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(null);
    }

    @PostMapping("/powerOff")
	public ResponseEntity<Void> powerOff(@Valid @RequestBody DeviceRequest deviceRequest) {
    	LOGGER.info("Begin ...");
    	senderService.powerOff(deviceRequest.getDeviceName());
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(null);
    }
    
    @PostMapping("/togglePower")
	public ResponseEntity<Void> togglePower(@Valid @RequestBody DeviceRequest deviceRequest) {
    	LOGGER.info("Begin ...");
    	senderService.togglePower(deviceRequest.getDeviceName());
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(null);
    }

    @PostMapping("/triggerSensorData")
	public ResponseEntity<Void> triggerSensorData(@Valid @RequestBody DeviceRequest deviceRequest) {
    	LOGGER.info("Begin ...");
    	senderService.triggerSensorData(deviceRequest.getDeviceName());
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(null);
    }
    
    @PostMapping("/triggerPowerState")
	public ResponseEntity<Void> triggerPowerState(@Valid @RequestBody DeviceRequest deviceRequest) {
    	LOGGER.info("Begin ...");
    	senderService.getPowerState(deviceRequest.getDeviceName());
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(null);
    }
    
    @GetMapping("/getPowerState")
	public ResponseEntity<String> getPowerState(@RequestParam String deviceName) {
    	LOGGER.info("Begin ...");
    	// format data to json format
    	var value = stateAndTelemetryCache.getData(topicHelper.getTopic(TopicEnum.STATE_POWER_TOPIC, deviceName));
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(value);
    }
    
    @GetMapping("/getSensorData")
	public ResponseEntity<String> getSensorData(@RequestParam String deviceName) {
    	LOGGER.info("Begin ...");
    	var value = stateAndTelemetryCache.getData(topicHelper.getTopic(TopicEnum.TELEMETRY_SENSOR_TOPIC, deviceName));
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(value);
    }
    
    @GetMapping("/dumpCache")
	public ResponseEntity<Void> dumpCache() {
    	LOGGER.info("Begin ...");
    	stateAndTelemetryCache.dumpCache();
    	LOGGER.info("End ...");
    	return ResponseEntity.ok(null);
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
}
