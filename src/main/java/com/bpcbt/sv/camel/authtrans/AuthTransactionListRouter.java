package com.bpcbt.sv.camel.authtrans;

import org.apache.camel.component.wmq.WmqComponent;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthTransactionListRouter extends SpringRouteBuilder {

	private final WmqComponent wmqComponent;

	@Autowired
	public AuthTransactionListRouter(WmqComponent wmqComponent) {
		this.wmqComponent = wmqComponent;
	}

	@Override
	public void configure() throws Exception {
		// TODO
	}

}
