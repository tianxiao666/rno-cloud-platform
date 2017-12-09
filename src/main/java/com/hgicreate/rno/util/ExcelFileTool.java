package com.hgicreate.rno.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ExcelFileTool {

	/**
	 * 创建excel文件
	 * 
	 * @param response
	 * @param data key:sheet名称，value:对应sheet数据
	 */
	public static boolean createExcel(HttpServletResponse response, Map<String, List<Map<String, Object>>> data) {

		OutputStream os = null;
		try {
			os = response.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		Workbook workbook = new SXSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		Row row;
		Cell cell;
		int n = 0;

		for (String s : data.keySet()) {
			int titleIndex = 0, rowIndex = 0;
			if (n > 0) {
				sheet = workbook.createSheet();
			}
			workbook.setSheetName(n, s);
			n++;
			for (Map<String, Object> map : data.get(s)) {
				int valueIndex = 0;
				if (rowIndex == 0) {
					row = sheet.createRow(0);
					for (String title : map.keySet()) {
						cell = row.createCell(titleIndex++);
						cell.setCellValue(title);
					}
				}
				row = sheet.createRow(++rowIndex);
				for (Object o : map.values()) {
					cell = row.createCell(valueIndex++);
					cell.setCellValue(o.toString());
				}
			}
		}

		// 最终写入文件
		try {
			workbook.write(os);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
