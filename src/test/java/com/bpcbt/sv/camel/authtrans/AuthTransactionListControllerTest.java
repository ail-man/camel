package com.bpcbt.sv.camel.authtrans;

import java.util.Properties;

import static com.bpcbt.sv.camel.authtrans.AuthTransactionListController.PROP_CHANNEL;
import static com.bpcbt.sv.camel.authtrans.AuthTransactionListController.PROP_CIPHER;
import static com.bpcbt.sv.camel.authtrans.AuthTransactionListController.PROP_HOSTNAME;
import static com.bpcbt.sv.camel.authtrans.AuthTransactionListController.PROP_PASS;
import static com.bpcbt.sv.camel.authtrans.AuthTransactionListController.PROP_PORT;
import static com.bpcbt.sv.camel.authtrans.AuthTransactionListController.PROP_QMANAGER;
import static com.bpcbt.sv.camel.authtrans.AuthTransactionListController.PROP_QUEUENAME;
import static com.bpcbt.sv.camel.authtrans.AuthTransactionListController.PROP_USER;
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
public class AuthTransactionListControllerTest {

	private static final String HOSTNAME = "mint";
	private static final String PORT = "1414";
	private static final String QUEUE_MANAGER = "MQTest";
	private static final String CHANNEL = "HPT5.CLNT.WL";
	private static final String CIPHER = "TLS_RSA_WITH_AES_128_CBC_SHA256";
	private static final String MQ_USER = "mquser";
	private static final String MQ_PASS = "mquser";
	private static final String QUEUE_NAME = "MQTestQueue";

	@Autowired
	private CamelContext camelContext;

	@Autowired
	private AuthTransactionListController authTransactionListController;

	@Before
	public void setUp() throws Exception {
		System.setProperty("javax.net.ssl.keyStore", "src/test/resources/APP1.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "mint");
		System.setProperty("javax.net.ssl.trustStore", "src/test/resources/APP1.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "mint");

		System.setProperty("javax.net.debug", "all");
		System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", "false");
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

		Properties properties = new Properties();
		properties.put(PROP_HOSTNAME, HOSTNAME);
		properties.put(PROP_PORT, PORT);
		properties.put(PROP_QMANAGER, QUEUE_MANAGER);
		properties.put(PROP_CHANNEL, CHANNEL);
		properties.put(PROP_CIPHER, CIPHER);
		properties.put(PROP_USER, MQ_USER);
		properties.put(PROP_PASS, MQ_PASS);
		properties.put(PROP_QUEUENAME, QUEUE_NAME);
		authTransactionListController.configure(properties);
	}

	@Test
	public void testApp() throws Exception {
		sendTestMessages();
		Thread.sleep(1000);
	}

	private void sendTestMessages() {
		ProducerTemplate template = camelContext.createProducerTemplate();
		for (int i = 0; i < 10; i++) {
			template.sendBodyAndHeader("wmq:" + QUEUE_NAME + "?username=" + MQ_USER + "&password=" + MQ_PASS, "I new test message " + i, "JMS_IBM_MQMD_ApplIdentityData", "anyIdData");
		}
	}

}