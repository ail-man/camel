package com.bpcbt.sv.camel.beans;

import java.util.ArrayList;
import java.util.List;

public class Route {
	private List<Field> fields;
	private String encoding;
	private String format;
	private String from;

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	public void addField(Field field){
		if(fields == null){
			fields = new ArrayList<Field>();
		}
		fields.add(field);
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Field field : fields){
			sb.append(field).append(" ");
		}
		return String.format("{from='%s', '%s', encoding='%s', format='%s'}", from, sb.toString(), encoding, format);
	}
}
