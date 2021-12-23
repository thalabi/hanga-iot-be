package com.kerneldc.mqttpublisher.springconfig;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.kerneldc.mqttpublisher.controller.Device;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter @Setter
@ConfigurationProperties(prefix = "esp")
public class DeviceListPropertyHolder {
	private List<Device> deviceList;
}
