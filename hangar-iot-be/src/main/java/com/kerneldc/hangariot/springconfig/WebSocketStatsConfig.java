package com.kerneldc.hangariot.springconfig;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

@Configuration
public class WebSocketStatsConfig {

	@Autowired
	private WebSocketMessageBrokerStats webSocketMessageBrokerStats;
	@Autowired
	private Environment environment;
	@PostConstruct
	public void init() {
		if (environment.getActiveProfiles().length == 0 || Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
			webSocketMessageBrokerStats.setLoggingPeriod(10 * 1000l); // 10 seconds
		} else if (Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
			webSocketMessageBrokerStats.setLoggingPeriod(300 * 1000l); // 5 minutes
		}
		// default is 30 minutes
	}

}
