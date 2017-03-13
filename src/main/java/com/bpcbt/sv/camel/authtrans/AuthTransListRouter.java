package com.bpcbt.sv.camel.authtrans;

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.wmq.WmqComponent;
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthTransListRouter {

	private final CamelContext camelContext;
	private final AuthTransListProcessor authTransListProcessor;
	private final DataFormat jaxbDataTypes;
	private final IbmWmqParams ibmWmqIncoming;
	private final IbmWmqParams ibmWmqOutgoing;

	@Autowired
	public AuthTransListRouter(CamelContext camelContext, AuthTransListProcessor authTransListProcessor, DataFormat jaxbDataTypes, IbmWmqParams ibmWmqIncoming, IbmWmqParams ibmWmqOutgoing) {
		this.camelContext = camelContext;
		this.authTransListProcessor = authTransListProcessor;
		this.jaxbDataTypes = jaxbDataTypes;
		this.ibmWmqIncoming = ibmWmqIncoming;
		this.ibmWmqOutgoing = ibmWmqOutgoing;
	}

	@PostConstruct
	public void init() throws Exception {
		camelContext.addComponent(ibmWmqIncoming.getComponentName(),
				WmqComponent.newWmqComponent(
						ibmWmqIncoming.getHost(),
						Integer.parseInt(ibmWmqIncoming.getPort()),
						ibmWmqIncoming.getQueueManager(),
						ibmWmqIncoming.getChannel(),
						ibmWmqIncoming.getSslCipherSuite()
				)
		);
		camelContext.addComponent(ibmWmqOutgoing.getComponentName(),
				WmqComponent.newWmqComponent(
						ibmWmqOutgoing.getHost(),
						Integer.parseInt(ibmWmqOutgoing.getPort()),
						ibmWmqOutgoing.getQueueManager(),
						ibmWmqOutgoing.getChannel(),
						ibmWmqOutgoing.getSslCipherSuite()
				)
		);
		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(ibmWmqIncoming.getComponentName() + ":" + ibmWmqIncoming.getQueueName()
						+ "?username=" + ibmWmqIncoming.getUsername()
						+ "&password=" + ibmWmqIncoming.getPassword())
						.routeId("authTransListRoute")
						.unmarshal(jaxbDataTypes)
						.process(authTransListProcessor)
						.dynamicRouter(method(AuthTransListDynamicRoute.class, "routeTo"));
			}
		});
	}

	IbmWmqParams getIbmWmqIncoming() {
		return ibmWmqIncoming;
	}

	CamelContext getCamelContext() {
		return camelContext;
	}
}
