package com.bpcbt.sv.camel.authtrans;

import org.apache.camel.component.wmq.WmqComponent;
import org.apache.camel.spring.SpringRouteBuilder;

public class AuthTransactionListRouterBuilder extends SpringRouteBuilder {
	static final String MQ_USER = "mquser";
	static final String MQ_PASS = "mquser";

	private final WmqComponent wmq;

	public AuthTransactionListRouterBuilder(WmqComponent wmq) {
		this.wmq = wmq;
	}

	@Override
	public void configure() throws Exception {
		from("wmq:MQTestQueue?username=" + MQ_USER + "&password=" + MQ_PASS)
				.to("stream:out");
	}

}
