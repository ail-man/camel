package com.bpcbt.sv.camel.authtrans;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class AuthTransactionListRouterBuilder extends RouteBuilder {

	private final String user;
	private final String pass;

	public AuthTransactionListRouterBuilder(String user, String pass) {
		this.user = user;
		this.pass = pass;
	}

	@Override
	public void configure() throws Exception {
		from("wmq:MQTestQueue?username=" + user + "&password=" + pass)
				.to("stream:out");
	}
}
