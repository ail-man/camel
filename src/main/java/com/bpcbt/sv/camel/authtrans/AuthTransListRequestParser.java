package com.bpcbt.sv.camel.authtrans;

import com.bpcbt.sv.camel.authtrans.schema.GetAuthTransactionListRq;
import com.bpcbt.sv.camel.authtrans.schema.ObjectFactory;

public class AuthTransListRequestParser {

	public GetAuthTransactionListRq getAuthTransactionListRq() {
		return new ObjectFactory().createGetAuthTransactionListRq();
	}
}
