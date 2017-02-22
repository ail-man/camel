package com.bpcbt.sv.camel.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtils {
	public static String readFile(String file, String encoding) throws Exception {
		BufferedReader in = null;
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(file), encoding);
			in = new BufferedReader(reader);
			char[] buffer = new char[10000];
			StringBuilder sb = new StringBuilder();
			int cnt;
			while ((cnt = in.read(buffer)) > 0) {
				sb.append(buffer, 0, cnt);
			}
			return sb.toString();
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public static void writeFile(String content, String file, String encoding) throws Exception {
		BufferedWriter wr = null;
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(file), encoding);
			wr = new BufferedWriter(writer);
			wr.write(content);
			wr.flush();
		} catch (Exception ex) {
			if (wr != null) {
				wr.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
	}
}
