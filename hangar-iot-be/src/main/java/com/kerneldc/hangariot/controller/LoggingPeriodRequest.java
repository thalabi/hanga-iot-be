package com.kerneldc.hangariot.controller;

import javax.validation.constraints.Positive;

import lombok.Data;

@Data
public class LoggingPeriodRequest {

	@Positive
	private Long loggingPeriodSecs;
}
