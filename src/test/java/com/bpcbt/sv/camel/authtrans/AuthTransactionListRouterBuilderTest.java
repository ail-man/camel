package com.bpcbt.sv.camel.authtrans;

import static com.bpcbt.sv.camel.authtrans.AuthTransactionListRouterBuilder.MQ_PASS;
import static com.bpcbt.sv.camel.authtrans.AuthTransactionListRouterBuilder.MQ_USER;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContextTest.xml" })
public class AuthTransactionListRouterBuilderTest {

	@Autowired
	private CamelContext camelContext;

	@Before
	public void setUp() throws Exception {
		ProducerTemplate template = camelContext.createProducerTemplate();
		for (int i = 0; i < 10; i++) {
			template.sendBodyAndHeader("wmq:MQTestQueue?username=" + MQ_USER + "&password=" + MQ_PASS, "I new test message " + i, "JMS_IBM_MQMD_ApplIdentityData", "anyIdData");
		}
	}

	@Test
	public void testApp() throws Exception {
		Thread.sleep(1000);
	}
}