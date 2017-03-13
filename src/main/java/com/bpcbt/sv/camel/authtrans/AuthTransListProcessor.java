package com.bpcbt.sv.camel.authtrans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.bpcbt.sv.camel.authtrans.schema.GetAuthTransactionListRq;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class AuthTransListProcessor implements Processor {

	private static final Logger logger = LogManager.getLogger(AuthTransListProcessor.class);

	@Resource(name = "boDataSource")
	private DataSource boDataSource;

	@Override
	public void process(Exchange exchange) throws Exception {
		GetAuthTransactionListRq request = (GetAuthTransactionListRq) exchange.getIn().getBody();
		logger.info("AuthTransactionListProcessor: " + request.getRqUID());
		String someData = getSomeDataFromDb();
		exchange.getOut().setBody(someData + ": " + request.getSPName());

		exchange.setProperty("properties", new HashMap<String, Object>());
	}

	private String getSomeDataFromDb() {
		String result = "";

		String query = "select * from ACC_ACCOUNT";

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = boDataSource.getConnection();
			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getString("ID");
			}
		} catch (Exception e) {
			logger.error(e, e);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error(e, e);
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				logger.error(e, e);
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				logger.error(e, e);
			}
		}

		return result;
	}

}
