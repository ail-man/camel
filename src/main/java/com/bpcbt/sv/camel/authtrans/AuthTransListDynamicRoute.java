package com.bpcbt.sv.camel.authtrans;

import java.util.Map;

import org.apache.camel.ExchangeProperty;

public class AuthTransListDynamicRoute {

	@SuppressWarnings("unused")
	public String routeTo(String body, @ExchangeProperty("properties") Map<String, Object> properties) {
		int invoked = 0;
		Object current = properties.get("invoked");
		if (current != null) {
			invoked = Integer.parseInt(current.toString());
		}
		invoked++;
		properties.put("invoked", invoked);

		if (invoked == 1) {
			return "stream:out"; // TODO "wmqTo:<queueName>?username=...&password=..."
		}

		return null;
	}

}
