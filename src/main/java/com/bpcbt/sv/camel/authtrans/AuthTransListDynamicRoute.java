package com.bpcbt.sv.camel.authtrans;

import java.util.Map;

import com.bpcbt.sv.camel.authtrans.schema.GetAuthTransactionListRs;
import org.apache.camel.ExchangeProperty;
import org.apache.camel.Header;
import org.apache.commons.lang3.StringUtils;

public class AuthTransListDynamicRoute {
	static final String HEADER_IBM_WMQ_OUTGOING = "ibmWmqOutgoing";

	@SuppressWarnings("unused")
	public String routeTo(GetAuthTransactionListRs response, @Header(HEADER_IBM_WMQ_OUTGOING) IbmWmqParams ibmWmqParams, @ExchangeProperty("properties") Map<String, Object> properties) {
		int invoked = 0;
		Object current = properties.get("invoked");
		if (current != null) {
			invoked = Integer.parseInt(current.toString());
		}
		invoked++;
		properties.put("invoked", invoked);

		if (invoked == 1) {
			//			return "stream:out";
			String spName = response.getSPName();
			String queueName;
			if (Integer.parseInt(spName.substring(33)) % 2 == 0) {
				queueName = "MQOutgoingTestQueue2";
			} else {
				queueName = "MQOutgoingTestQueue1";
			}
			return StringUtils.isEmpty(ibmWmqParams.getQueueName()) ? ibmWmqParams.buildUri(queueName) : ibmWmqParams.buildUri();
		}

		return null;
	}

}
