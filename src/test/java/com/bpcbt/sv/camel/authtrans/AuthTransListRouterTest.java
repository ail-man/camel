package com.bpcbt.sv.camel.authtrans;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContextTest.xml" })
public class AuthTransListRouterTest {

	@Autowired
	private AuthTransListRouter component;

	private static String getExampleRequest(int i) {
		return "<?xml version=\"1.0\"?>\n"
				+ "<GetAuthTransactionListRq>\n"
				+ "  <RqUID>731r8913hr89u1y3r813r9h138r7c3789r7g13r89g78" + i + "</RqUID>\n"
				+ "  <RqTm>string</RqTm>\n"
				+ "  <OperUID>string</OperUID>\n"
				+ "  <SPName>!!!SOME_SP_NAME!!!-" + i + "</SPName>\n"
				+ "  <!--Optional:-->\n"
				+ "  <SystemId>string</SystemId>\n"
				+ "  <BankInfo>\n"
				+ "    <!--Optional:-->\n"
				+ "    <BranchId>string</BranchId>\n"
				+ "    <RegionId>stri</RegionId>\n"
				+ "  </BankInfo>\n"
				+ "  <CardInfo>\n"
				+ "    <CardId>10</CardId>\n"
				+ "    <EndDt>stri</EndDt>\n"
				+ "  </CardInfo>\n"
				+ "  <!--Optional:-->\n"
				+ "  <AuthenticityCode>string</AuthenticityCode>\n"
				+ "</GetAuthTransactionListRq>\n";
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
					getExampleRequest(i),
					"JMS_IBM_MQMD_ApplIdentityData",
					"anyIdData"
			);
		}
	}

}