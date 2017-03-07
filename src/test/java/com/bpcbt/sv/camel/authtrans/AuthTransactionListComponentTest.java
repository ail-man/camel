package com.bpcbt.sv.camel.authtrans;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContextTest.xml" })
public class AuthTransactionListComponentTest {

	@Autowired
	private AuthTransactionListComponent component;

	@Before
	public void setUp() throws Exception {
		System.setProperty("javax.net.ssl.keyStore", "src/test/resources/APP1.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "mint");
		System.setProperty("javax.net.ssl.trustStore", "src/test/resources/APP1.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "mint");

		System.setProperty("javax.net.debug", "all");
		System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", "false");
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

		component.configure();
	}

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