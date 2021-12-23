package com.kerneldc.mqttpublisher;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.kerneldc.mqttpublisher.mqtt.service.DeviceService;
import com.kerneldc.mqttpublisher.mqtt.service.SenderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class InitializeApplication implements ApplicationRunner {

	private final SenderService senderService;
	private final DeviceService deviceService;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		var deviceList = deviceService.getDeviceNameList();
		LOGGER.info("Managing devices: {}", String.join(", ", deviceList));

		LOGGER.info("Getting power state and sensor data for devices");
		for (var device: deviceService.getDeviceNameList()) {
			senderService.getPowerState(device);
			senderService.triggerSensorData(device);
		}
	}

}
