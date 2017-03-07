package com.bpcbt.sv.camel.authtrans;

import org.apache.camel.CamelContext;
import org.apache.camel.component.wmq.WmqComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

@Controller
public class AuthTransactionListController {

	private static final String COMPONENT_NAME = "wmq";

	private final CamelContext camelContext;
	private final ApplicationContext springContext;
	private final AuthTransactionListProcessor authTransactionListProcessor;

	@Value("${ibm_wmq_host}")
	private String ibmWmqHost;
	@Value("${ibm_wmq_port}")
	private String ibmWmqPort;
	@Value("${ibm_wmq_queue_manager}")
	private String ibmWmqQueueManager;
	@Value("${ibm_wmq_channel}")
	private String ibmWmqChannel;
	@Value("${ibm_wmq_ssl_cipher_suite}")
	private String ibmWmqSslCipherSuite;
	@Value("${ibm_wmq_username}")
	private String ibmWmqUsername;
	@Value("${ibm_wmq_password}")
	private String ibmWmqPassword;
	@Value("${ibm_wmq_incoming_queue_name}")
	private String ibmWmqIncomingQueueName;

	@Autowired
	public AuthTransactionListController(CamelContext camelContext, ApplicationContext springContext, AuthTransactionListProcessor authTransactionListProcessor) {
		this.camelContext = camelContext;
		this.springContext = springContext;
		this.authTransactionListProcessor = authTransactionListProcessor;
	}

	public void configure() throws Exception {
		camelContext.addComponent(COMPONENT_NAME, WmqComponent.newWmqComponent(ibmWmqHost, Integer.parseInt(ibmWmqPort), ibmWmqQueueManager, ibmWmqChannel, ibmWmqSslCipherSuite));
		camelContext.addRoutes(springContext.getBean(AuthTransactionListRouterBuilder.class, ibmWmqUsername, ibmWmqPassword, ibmWmqIncomingQueueName, authTransactionListProcessor));
	}

	String getComponentName() {
		return COMPONENT_NAME;
	}

	String getQueueName() {
		return ibmWmqIncomingQueueName;
	}

	String getUsername() {
		return ibmWmqUsername;
	}

	String getPassword() {
		return ibmWmqPassword;
	}

	CamelContext getCamelContext() {
		return camelContext;
	}
}
