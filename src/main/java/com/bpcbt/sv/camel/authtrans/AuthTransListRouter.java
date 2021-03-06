package com.bpcbt.sv.camel.authtrans;

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.wmq.WmqComponent;
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthTransListRouter {

	private static final String COMPONENT_NAME = "wmq";

	private final CamelContext camelContext;
	private final AuthTransListProcessor authTransListProcessor;
	private final DataFormat jaxbDataTypes;

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
	public AuthTransListRouter(CamelContext camelContext, AuthTransListProcessor authTransListProcessor, DataFormat jaxbDataTypes) {
		this.camelContext = camelContext;
		this.authTransListProcessor = authTransListProcessor;
		this.jaxbDataTypes = jaxbDataTypes;
	}

	@PostConstruct
	public void init() throws Exception {
		camelContext.addComponent(COMPONENT_NAME, WmqComponent.newWmqComponent(ibmWmqHost, Integer.parseInt(ibmWmqPort), ibmWmqQueueManager, ibmWmqChannel, ibmWmqSslCipherSuite));
		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(COMPONENT_NAME + ":" + ibmWmqIncomingQueueName + "?username=" + ibmWmqUsername + "&password=" + ibmWmqPassword)
						.routeId("authTransListRoute")
						.unmarshal(jaxbDataTypes)
						.process(authTransListProcessor)
						.dynamicRouter(method(AuthTransListDynamicRoute.class, "routeTo"));
			}
		});
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
