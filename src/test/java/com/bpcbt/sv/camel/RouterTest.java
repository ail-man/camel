package com.bpcbt.sv.camel;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.wmq.WmqComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RouterTest extends CamelSpringTestSupport {

	private static final String HOSTNAME = "mint";
	private static final int PORT = 1414;
	private static final String QUEUE_MANAGER = "MQTest";
	private static final String CHANNEL = "HPT5.CLNT.WL";
	private static final String MQ_PASS = "mquser";
	private static final String MQ_USER = MQ_PASS;

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("/applicationContextTest.xml");
	}

	@Test
	public void testCamel() throws Exception {
		CamelContext context = new DefaultCamelContext();

		WmqComponent wmqComponent = WmqComponent.newWmqComponent(HOSTNAME, PORT, QUEUE_MANAGER, CHANNEL).excludeRFHeaders();
		context.addComponent("wmq", wmqComponent);

		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("wmq:MQTestQueue?username=" + MQ_USER + "&password=" + MQ_PASS)
						.to("file://target/testRoute");
			}
		});
		ProducerTemplate template = context.createProducerTemplate();
		for (int i = 0; i < 10; i++) {
			template.sendBodyAndHeader("wmq:MQTestQueue?username=" + MQ_USER + "&password=" + MQ_PASS, "I new test message " + i, "JMS_IBM_MQMD_ApplIdentityData", "anyIdData");
		}
		context.start();
		// wait a bit and then stop
		Thread.sleep(1000);
		context.stop();
	}

	@Test
	public void testCamel2() throws Exception {
		// START SNIPPET: e1
		CamelContext context = new DefaultCamelContext();
		// END SNIPPET: e1
		// Set up the ActiveMQ JMS Components
		// START SNIPPET: e2
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
		// Note we can explicit name the component
		context.addComponent("test-jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
		// END SNIPPET: e2
		// Add some configuration by hand ...
		// START SNIPPET: e3
		context.addRoutes(new RouteBuilder() {
			public void configure() {
				from("test-jms:queue:test.queue").to("file://target/testRoute");
			}
		});
		// END SNIPPET: e3
		// Camel template - a handy class for kicking off exchanges
		// START SNIPPET: e4
		ProducerTemplate template = context.createProducerTemplate();
		// END SNIPPET: e4
		// Now everything is set up - lets start the context
		context.start();
		// Now send some test text to a component - for this case a JMS Queue
		// The text get converted to JMS messages - and sent to the Queue
		// test.queue
		// The file component is listening for messages from the Queue
		// test.queue, consumes
		// them and stores them to disk. The content of each file will be the
		// test we sent here.
		// The listener on the file component gets notified when new files are
		// found ... that's it!
		// START SNIPPET: e5
		for (int i = 0; i < 10; i++) {
			template.sendBody("test-jms:queue:test.queue", "Test Message: " + i);
		}
		// END SNIPPET: e5

		// wait a bit and then stop
		Thread.sleep(1000);
		context.stop();
	}
}