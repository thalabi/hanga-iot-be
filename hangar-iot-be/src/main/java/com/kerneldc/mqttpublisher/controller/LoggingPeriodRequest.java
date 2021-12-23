package com.kerneldc.mqttpublisher.controller;

import javax.validation.constraints.Positive;

import lombok.Data;

@Data
public class LoggingPeriodRequest {

	@Positive
	private Long loggingPeriodSecs;
}
