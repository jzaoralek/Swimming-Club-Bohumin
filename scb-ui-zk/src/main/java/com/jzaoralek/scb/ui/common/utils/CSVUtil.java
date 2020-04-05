package com.jzaoralek.scb.ui.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;

import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.ui.common.WebConstants;

public final class CSVUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ExcelUtil.class);
	
	private CSVUtil() {};

	private static final char DEFAULT_SEPARATOR = ';';

	public static void exportToCSV(String filename, Map<String, Object[]> data) {
		if (filename == null) {
			throw new IllegalArgumentException("filename is null");
		}
		ByteArrayOutputStream stream = null;
		
		try {
			stream = new ByteArrayOutputStream();
			
			Map<String, List<String>> strData = new LinkedHashMap<>();
			List<String> rowItemList = null;
			
			for (String key : data.keySet()) {
				Object[] objArr = data.get(key);
				int cellnum = 0;
				Object item = null;
				rowItemList = new ArrayList<>();
				for (Object obj : objArr) {
					item = objArr[cellnum];
					if (item != null) {
						rowItemList.add(obj.toString());
					} else {
						rowItemList.add("");
					}
					cellnum++;
				}
				strData.put(key, rowItemList);
			}

			for (String key : strData.keySet()) {
				writeLine(stream, strData.get(key));
			}

			Attachment attachment = new Attachment();
			attachment.setByteArray(stream.toByteArray());
			attachment.setContentType("application/file");
			attachment.setName(filename);
			WebUtils.downloadAttachment(attachment);
		} catch (FileNotFoundException e) {
			LOG.error("exportToExcel():: FileNotFoundException caught.", e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			LOG.error("exportToExcel():: IOException caught.", e);
			throw new RuntimeException(e);
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				LOG.error("exportToExcel():: IOException caught.", e);
				throw new RuntimeException(e);
			}
		}
	}
	
	private static void writeLine(ByteArrayOutputStream w, List<String> values) throws IOException {
		writeLine(w, values, ' ');
	}

	// https://tools.ietf.org/html/rfc4180
	private static String followCVSformat(String value) {

		String result = value;
		if (result.contains("\"")) {
			result = result.replace("\"", "\"\"");
		}
		return result;

	}

	private static void writeLine(ByteArrayOutputStream w, List<String> values, char customQuote) throws IOException {

		boolean first = true;
		
		StringBuilder sb = new StringBuilder();
		for (String value : values) {
			if (!first) {
				sb.append(DEFAULT_SEPARATOR);
			}
			if (customQuote == ' ') {
				sb.append(followCVSformat(value));
			} else {
				sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
			}

			first = false;
		}
		sb.append("\n");
		w.write(sb.toString().getBytes("UTF-8"));
	}
}
