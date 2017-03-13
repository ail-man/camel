package com.bpcbt.sv.camel.authtrans;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class IbmWmqParamsTest {

	@Test
	public void buildUri() throws Exception {
		IbmWmqParams params = new IbmWmqParams();
		params.setComponentName("someComponentName");
		params.setQueueName("someQueueName");
		params.setUsername("someUser");
		params.setPassword("somePass");

		assertThat(params.buildUri(), is("someComponentName:someQueueName?username=someUser&password=somePass"));
		assertThat(params.buildUri("otherQueueName"), is("someComponentName:otherQueueName?username=someUser&password=somePass"));
	}

}