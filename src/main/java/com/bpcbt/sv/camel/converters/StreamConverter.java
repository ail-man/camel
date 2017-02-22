package com.bpcbt.sv.camel.converters;

import java.io.InputStream;
import java.io.OutputStream;

public interface StreamConverter {
	void convert(InputStream input, OutputStream output) throws Exception;

	void reloadConfig(String file) throws Exception;

	void invalidate(String sessionId) throws Exception;

	String getHeader(String key);

	void setHeader(String key, String value);
}