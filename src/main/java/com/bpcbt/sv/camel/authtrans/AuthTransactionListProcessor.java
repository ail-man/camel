package com.bpcbt.sv.camel.authtrans;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class AuthTransactionListProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		System.out.println("AuthTransactionListProcessor: " + exchange.getIn().getBody());
	}

}
