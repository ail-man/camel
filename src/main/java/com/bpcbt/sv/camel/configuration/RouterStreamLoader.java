package com.bpcbt.sv.camel.configuration;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.bpcbt.sv.camel.beans.Field;
import com.bpcbt.sv.camel.beans.Route;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RouterStreamLoader {
	private static final Logger logger = LogManager.getLogger(RouterStreamLoader.class);
	private static final String ROOT_ELEMENT = "routes";
	private static final String SUB_ELEMENT = "route";
	@Value("${routes_file}")
	private String routesFilePath;
	private List<Route> routes;

	public static void main(String[] args) throws Exception {
		RouterStreamLoader loader = new RouterStreamLoader();
		for (Route route : loader.loadRoutes()) {
			System.out.println(route);
		}
	}

	public List<Route> loadRoutes() throws Exception {
		if (routes == null) {
			loadFromXml();
		}
		return routes;
	}

	private void loadFromXml() throws Exception {
		FileInputStream fis = new FileInputStream(routesFilePath);
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory.IS_COALESCING, true);
		XMLStreamReader streamReader = factory.createXMLStreamReader(fis);
		Route route = null;
		Field field = null;

		try {
			while (streamReader.hasNext()) {
				streamReader.next();
				int xmlEvent = streamReader.getEventType();
				switch (xmlEvent) {
				case XMLStreamConstants.START_DOCUMENT:
					break;
				case XMLStreamConstants.START_ELEMENT:
					QName qname = streamReader.getName();
					String localName = qname.getLocalPart();
					if (localName.equalsIgnoreCase(ROOT_ELEMENT)) {
						routes = new ArrayList<>();
						break;
					}
					if (localName.equalsIgnoreCase(SUB_ELEMENT)) {
						route = new Route();
						routes.add(route);
					} else if (localName.equalsIgnoreCase("encoding")) {
						skipWhiteSpace(streamReader);
						assert route != null;
						route.setEncoding(streamReader.getText());
					} else if (localName.equalsIgnoreCase("format")) {
						skipWhiteSpace(streamReader);
						assert route != null;
						route.setFormat(streamReader.getText());
					} else if (localName.equalsIgnoreCase("from")) {
						skipWhiteSpace(streamReader);
						assert route != null;
						route.setFrom(streamReader.getText());
					} else {
						field = new Field();
						field.setName(localName);
						assert route != null;
						route.addField(field);
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					if (streamReader.isWhiteSpace()) {
						break;
					}
					String value = streamReader.getText();
					assert field != null;
					field.setValue(value);
					break;
				case XMLStreamConstants.END_ELEMENT:
					break;
				case XMLStreamConstants.END_DOCUMENT:
					break;
				}
			}
		} finally {
			try {
				streamReader.close();
				fis.close();
			} catch (Exception ignored) {
			}
		}
	}

	public void skipWhiteSpace(XMLStreamReader streamReader) throws XMLStreamException {
		boolean skip = true;
		do {
			streamReader.next();
			if (!streamReader.isWhiteSpace()) {
				skip = false;
			}
		} while (streamReader.hasNext() && skip);
	}

}
