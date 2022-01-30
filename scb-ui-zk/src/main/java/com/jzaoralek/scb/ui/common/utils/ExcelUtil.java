package com.jzaoralek.scb.ui.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;

import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.dataservice.domain.Attachment;

public final class ExcelUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ExcelUtil.class);

	private ExcelUtil(){};

	public static void exportToExcel(String filename, Map<String, Object[]> data) {
		HSSFWorkbook workbook = buildWorkbook(data);
		if (filename == null) {
			throw new IllegalArgumentException("filename is null");
		}
		ByteArrayOutputStream stream = null;
		try {
			stream = new ByteArrayOutputStream();
			workbook.write(stream);

			Attachment attachment = new Attachment();
			attachment.setByteArray(stream.toByteArray());
			attachment.setContentType("application/file");
			attachment.setName(filename);
			Executions.getCurrent().getSession().setAttribute(WebConstants.ATTACHMENT_PARAM, attachment);
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
	
	public static void exportToCSV(String filename, Map<String, Object[]> data) {
		HSSFWorkbook workbook = buildWorkbook(data);
		if (filename == null) {
			throw new IllegalArgumentException("filename is null");
		}
		ByteArrayOutputStream stream = null;
		try {
			stream = new ByteArrayOutputStream();

			HSSFSheet sheet = workbook.getSheetAt(0);
			StringBuilder strBuff = new StringBuilder();
			for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			    HSSFRow row = sheet.getRow(i);
			    for (int j = 0; j <= row.getLastCellNum(); j++) {
			        HSSFCell cell = row.getCell(j);
			        if (cell != null) {
			        	strBuff.append(cell.getStringCellValue() + ";");			        	
			        }
			    }
			    strBuff.append("\n");
			}
			stream.write(strBuff.toString().getBytes("UTF-8"));
			
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
	
	
	

	private static HSSFWorkbook buildWorkbook(Map<String, Object[]> data) {
		if (data == null) {
			throw new IllegalArgumentException("data is null");
		}

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Sample sheet");

		Set<String> keyset = data.keySet();
		int rownum = 0;
		for (String key : keyset) {
			Row row = sheet.createRow(rownum++);
			Object[] objArr = data.get(key);
			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
				if (obj instanceof Date)
					cell.setCellValue((Date) obj);
				else if (obj instanceof Boolean)
					cell.setCellValue((Boolean) obj);
				else if (obj instanceof String)
					cell.setCellValue((String) obj);
				else if (obj instanceof Double)
					cell.setCellValue((Double) obj);
				else if (obj instanceof Integer)
					cell.setCellValue((Integer) obj);
				else if (obj instanceof Long)
					cell.setCellValue((Long) obj);
			}
		}

		return workbook;
	}

}
