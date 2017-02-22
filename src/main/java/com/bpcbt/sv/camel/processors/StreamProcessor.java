package com.bpcbt.sv.camel.processors;

import com.bpcbt.sv.camel.converters.StreamConverter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Component
@Scope("prototype")
public class StreamProcessor implements Processor {
	private static final Logger logger = LogManager.getLogger(StreamProcessor.class);
	private StreamConverter converter;

	public void init(Class converterClass) throws Exception {
		logger.info("Create converter for class " + converterClass.getName());
		converter = (StreamConverter) converterClass.newInstance();
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		InputStream input = null;
		ByteArrayOutputStream output = null;
		try {
			input = (InputStream) exchange.getIn().getBody();
			output = new ByteArrayOutputStream();
			Object breadcrumbId = exchange.getIn().getHeader("breadcrumbId");
			if(breadcrumbId != null){
				converter.setHeader("MessageID", breadcrumbId.toString());
			}
			converter.convert(input, output);
			exchange.getOut().setHeader("JMSCorrelationID", converter.getHeader("CorrelationID")); 
			exchange.getOut().setBody(output.toByteArray());
		} catch (Exception ex) {
			logger.error("Error in converter", ex);
		} finally {
			input.close();
			output.close();
		}
	}

	public void reloadConfig(String file) {
		logger.info("Reload config for converter " + converter.getClass().getName() + " file " + file);
		try {
			converter.reloadConfig(file);
		} catch (Exception ex) {
			logger.error("Reload config error for converter " + converter.getClass().getName(), ex);
		}
	}

	public void invalidate(String sessionId) {
		logger.info(
				"Invalidate session for converter " + converter.getClass().getName() + ", sessionId is " + sessionId);
		try {
			converter.invalidate(sessionId);
		} catch (Exception ex) {
			logger.error("Invalidate session error for converter " + converter.getClass().getName(), ex);
		}
	}
}
