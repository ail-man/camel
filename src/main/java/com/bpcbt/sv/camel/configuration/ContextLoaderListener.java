package com.bpcbt.sv.camel.configuration;

import javax.servlet.ServletContextEvent;

public class ContextLoaderListener extends org.springframework.web.context.ContextLoaderListener {

	public static final String LOGS_PATH_PROP = "camel_logs_path";

	@Override
	public void contextInitialized(ServletContextEvent event) {
		configureLogger();
		super.contextInitialized(event);
	}

	private void configureLogger() {
		String logsPath = System.getProperty(LOGS_PATH_PROP);
		if (logsPath == null)
			System.setProperty(LOGS_PATH_PROP, "/home/" + System.getProperty("user.name") + "/camel/logs");
	}
}
