package com.bpcbt.sv.camel.authtrans;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AuthTransactionListRouterTest extends CamelSpringTestSupport {

	@Autowired
	private AuthTransactionListRouter authTransactionListRouter;

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("/applicationContextTest.xml");
	}

	@Test
	public void testApp() throws Exception {
		System.out.println(authTransactionListRouter);
	}
}