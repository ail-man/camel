package com.bpcbt.sv.camel.authtrans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthTransactionListProcessor implements Processor {

	private static final Logger logger = LogManager.getLogger(AuthTransactionListProcessor.class);

	private final DataSource boDataSource;

	@Autowired
	public AuthTransactionListProcessor(DataSource boDataSource) {
		this.boDataSource = boDataSource;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		System.out.println("AuthTransactionListProcessor: " + exchange.getIn().getBody());
		String someData = getSomeDataFromDb();
		exchange.getOut().setBody(someData);
	}

	private String getSomeDataFromDb() {
		String result = "";

		String query = "select * from T_REPORT_TEMPLATE";

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = boDataSource.getConnection();
			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getString("NAME");
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
