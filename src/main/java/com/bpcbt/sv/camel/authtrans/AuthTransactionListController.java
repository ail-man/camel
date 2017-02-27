package com.bpcbt.sv.camel.authtrans;

import java.util.Properties;

import org.apache.camel.CamelContext;
import org.apache.camel.component.wmq.WmqComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

@Controller
public class AuthTransactionListController {

	public static final String COMPONENT_NAME = "wmq";
	public static final String PROP_HOSTNAME = "org.apache.camel.component.wmq.hostname";
	public static final String PROP_PORT = "org.apache.camel.component.wmq.port";
	public static final String PROP_QMANAGER = "org.apache.camel.component.wmq.qmanager";
	public static final String PROP_CHANNEL = "org.apache.camel.component.wmq.channel";
	public static final String PROP_USER = "org.apache.camel.component.wmq.user";
	public static final String PROP_PASS = "org.apache.camel.component.wmq.pass";

	private final CamelContext camelContext;
	private final ApplicationContext springContext;

	@Autowired
	public AuthTransactionListController(CamelContext camelContext, ApplicationContext springContext) {
		this.camelContext = camelContext;
		this.springContext = springContext;
	}

	public void configure(final Properties props) throws Exception {
		final String hostname = props.getProperty(PROP_HOSTNAME);
		final String port = props.getProperty(PROP_PORT);
		final String queueManager = props.getProperty(PROP_QMANAGER);
		final String channel = props.getProperty(PROP_CHANNEL);
		final String user = props.getProperty(PROP_USER);
		final String pass = props.getProperty(PROP_PASS);

		final WmqComponent wmqComponent = WmqComponent.newWmqComponent(hostname, Integer.parseInt(port), queueManager, channel);

		camelContext.addComponent(COMPONENT_NAME, wmqComponent);

		final AuthTransactionListRouterBuilder authTransactionListRouterBuilder = springContext.getBean(AuthTransactionListRouterBuilder.class, user, pass);

		camelContext.addRoutes(authTransactionListRouterBuilder);
	}

}
