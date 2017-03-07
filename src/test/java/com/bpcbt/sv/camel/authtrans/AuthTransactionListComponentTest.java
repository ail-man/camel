package com.bpcbt.sv.camel.authtrans;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContextTest.xml" })
public class AuthTransactionListComponentTest {

	@Autowired
	private AuthTransactionListComponent component;

	@Test
	public void testApp() throws Exception {
		sendTestMessages();
		Thread.sleep(1000);
	}

	private void sendTestMessages() {
		ProducerTemplate template = component.getCamelContext().createProducerTemplate();
		for (int i = 0; i < 10; i++) {
			template.sendBodyAndHeader(
					component.getComponentName() + ":" + component.getQueueName() + "?username=" + component.getUsername() + "&password=" + component.getPassword(),
					"I new test message " + i,
					"JMS_IBM_MQMD_ApplIdentityData",
					"anyIdData"
			);
		}
	}

}