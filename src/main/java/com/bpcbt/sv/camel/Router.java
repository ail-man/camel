package com.bpcbt.sv.camel;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import com.bpcbt.sv.camel.beans.Field;
import com.bpcbt.sv.camel.beans.Route;
import com.bpcbt.sv.camel.configuration.RouterStreamLoader;
import com.bpcbt.sv.camel.processors.StreamProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.model.ExpressionNode;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Router extends SpringRouteBuilder {
	public static final String SESSION_ID_ATTR = "session-id";
	private static final Logger logger = LogManager.getLogger(Router.class);
	private static final long EXPIRE_HOURS = 5;
	@Autowired
	private RouterStreamLoader loader;
	@Autowired
	private ApplicationContext context;
	private List<StreamProcessor> processors = new ArrayList<>();
	private Map<String, Long> invalidSessions = new ConcurrentSkipListMap<>();
	private ThreadLocal<XMLInputFactory> xmlFactoryProvider = new ThreadLocal<XMLInputFactory>() {
		@Override
		protected XMLInputFactory initialValue() {
			return XMLInputFactory.newInstance();
		}
	};

	@SuppressWarnings("UnusedDeclaration")
	public boolean isValidSession(Exchange exchange) {
		try {
			String sessionId = getSessionId(exchange);
			if (sessionId == null || sessionId.isEmpty()) {
				logger.info("Session ID is not found in package!!!");
				return true;
			}
			if (invalidSessions.containsKey(sessionId)) {
				logger.info("Session ID " + sessionId + " is not valid. Skip message!");
				return false;
			} else {
				logger.info("Session Id " + sessionId + " is valid.");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return true;
	}

	@Override
	public void configure() throws Exception {
		ThreadContext.put("revision", "---");

		try {
			// Resetting config
			Class.forName("com.bpcbt.sv.camel.converters.Config").getMethod("resetSettings").invoke(null);
		} catch (Exception e) {
			logger.error("Could not reset converters config. Some setting may not reload from config file. " + e.getMessage());
		}

		logger.info("Start loading routes");
		try {
			List<Route> routes = loader.loadRoutes();
			if (routes.isEmpty()) {
				throw new Exception("No routes in config");
			}
			for (Route route : routes) {
				ExpressionNode def = from(route.getFrom()).filter().method(this, "isValidSession");
				for (Field field : route.getFields()) {
					if (field.getName().equals("to")) {
						def.to(field.getValue());
					}
					if (field.getName().equals("converter")) {
						if (field.getValue().equalsIgnoreCase("TOSTRING")) {
							def.convertBodyTo(String.class, route.getEncoding());
						} else {
							StreamProcessor processor = context.getBean(StreamProcessor.class);
							processor.init(Class.forName(field.getValue()));
							processors.add(processor);
							def.convertBodyTo(InputStream.class, route.getEncoding()).process(processor);
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Error occurred", ex);
			throw ex;
		}
		logger.info("Routes loading completed");
	}

	public void reloadConvertersConfigs(String file) {
		for (StreamProcessor processor : processors) {
			processor.reloadConfig(file);
		}
	}

	public void invalidateSession(String sessionId) {
		//expire check
		for (String key : invalidSessions.keySet()) {
			long created = invalidSessions.get(key);
			if ((System.currentTimeMillis() - created) / 3600000L >= EXPIRE_HOURS) {
				invalidSessions.remove(key);
			}
		}
		invalidSessions.put(sessionId, System.currentTimeMillis());
		for (StreamProcessor processor : processors) {
			processor.invalidate(sessionId);
		}
	}

	// Extract session id from message without using Camel's built in XPath filter
	// because message may contain some charachters (encapsulated in cdata) that are not valid for xml charset
	// and since we do not encode cdata section with base64, we have to parse xml as stream, trying to find session id
	// before cdata section begins. But, this method is faster than xpath anyway
	private String getSessionId(Exchange exchange) throws Exception {
		Object body = exchange.getIn().getBody();
		if (body != null) {
			try (InputStream is = body instanceof byte[] ?
					new ByteArrayInputStream((byte[]) body) : exchange.getIn().getBody(InputStream.class))
			{
				XMLStreamReader reader = xmlFactoryProvider.get().createXMLStreamReader(is);
				try {
					StringBuilder sessionIdBuf = null;
					while (reader.hasNext()) {
						reader.next();
						int xmlEvent = reader.getEventType();
						switch (xmlEvent) {
						case XMLStreamConstants.START_ELEMENT:
							String name = reader.getName().getLocalPart();
							if (name.equals(SESSION_ID_ATTR))
								sessionIdBuf = new StringBuilder();
							break;
						case XMLStreamConstants.CHARACTERS:
							if (sessionIdBuf != null)
								sessionIdBuf.append(reader.getText());
							break;
						case XMLStreamConstants.END_ELEMENT:
							if (sessionIdBuf != null)
								return sessionIdBuf.toString();
							break;
						}
					}
				} finally {
					reader.close();
				}
			}
		}
		return null;
	}
}
