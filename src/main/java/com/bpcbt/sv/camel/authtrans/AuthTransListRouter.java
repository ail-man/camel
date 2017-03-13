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

	private static final String AUTH_TRANS_LIST_ROUTE_NAME = "authTransListRoute";
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
						ibmWmqIncoming.getPort(),
						ibmWmqIncoming.getQueueManager(),
						ibmWmqIncoming.getChannel(),
						ibmWmqIncoming.getSslCipherSuite()
				)
		);
		camelContext.addComponent(ibmWmqOutgoing.getComponentName(),
				WmqComponent.newWmqComponent(
						ibmWmqOutgoing.getHost(),
						ibmWmqOutgoing.getPort(),
						ibmWmqOutgoing.getQueueManager(),
						ibmWmqOutgoing.getChannel(),
						ibmWmqOutgoing.getSslCipherSuite()
				)
		);
		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(ibmWmqIncoming.buildUri())
						.routeId(AUTH_TRANS_LIST_ROUTE_NAME)
						.unmarshal(jaxbDataTypes)
						.process(authTransListProcessor)
						.unmarshal(jaxbDataTypes)
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
