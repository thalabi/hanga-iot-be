package com.kerneldc.hangariot.controller;


import javax.validation.constraints.NotBlank;

import com.kerneldc.hangariot.mqtt.result.TimersResult;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TimersRequest extends TimersResult {

	@NotBlank(message = "Device name is missing")
	private String deviceName;
	
	private Boolean timersModified;
	
	private Boolean timer1Modified;
	private Boolean timer2Modified;
	private Boolean timer3Modified;
	private Boolean timer4Modified;
	private Boolean timer5Modified;
	private Boolean timer6Modified;
	private Boolean timer7Modified;
	private Boolean timer8Modified;
	private Boolean timer9Modified;
	private Boolean timer10Modified;
	private Boolean timer11Modified;
	private Boolean timer12Modified;
	private Boolean timer13Modified;
	private Boolean timer14Modified;
	private Boolean timer15Modified;
	private Boolean timer16Modified;
}
