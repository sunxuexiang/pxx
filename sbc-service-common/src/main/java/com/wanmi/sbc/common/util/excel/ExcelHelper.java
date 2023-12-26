package com.wanmi.sbc.common.util.excel;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mac on 2017/5/6.
 */
@Slf4j
public class ExcelHelper<T> {

	/**
	 *
	 */
	private final HSSFWorkbook work;

	public ExcelHelper() {
		this.work = new HSSFWorkbook();
	}

	public HSSFWorkbook getWork() {
		return work;
	}

	/**
	 * @param sheetName
	 * @param dataList
	 */
	public void addSheet(String sheetName, Column[] columns, List<T> dataList) {
		int rowIndex = 0;
		HSSFSheet sheet = work.createSheet(sheetName);
		HSSFRow headRow = sheet.createRow(rowIndex++);

		//头
		int cellIndex = 0;
		for (Column c : columns) {
			HSSFCell cell = headRow.createCell(cellIndex++);
			cell.setCellValue(c.getHeader());
		}

		//体
		for (T data : dataList) {
			cellIndex = 0;
			HSSFRow row = sheet.createRow(rowIndex++);
			for (Column column : columns) {
				HSSFCell cell = row.createCell(cellIndex++);
				column.getRender().render(cell, data);
			}
		}

		//
	}

	/**
	 * @param sheetName
	 * @param dataList
	 */
	public void addSheet(String sheetName, SpanColumn[] columns, List<T> dataList,
			String listPropsExp) {
		int rowIndex = 0;
		HSSFSheet sheet = work.createSheet(sheetName);
		HSSFRow headRow = sheet.createRow(rowIndex++);

		//头
		int cellIndex = 0;
		for (SpanColumn c : columns) {
			HSSFCell cell = headRow.createCell(cellIndex++);
			cell.setCellValue(c.getHeader());
		}

		//体
		for (T data : dataList) {
			cellIndex = 0;
			List list = getListProps(listPropsExp, data);
			int mergeRowCount = list != null && !list.isEmpty() ? list.size() : 0;
			HSSFRow row = sheet.createRow(rowIndex);
			boolean hasMerged = false;
			for (SpanColumn column : columns) {
				String expString = column.getPropsExp();
				List listProps = getListProps(expString, data);
				//判断当前列是不是list列
				if (listProps != null && !listProps.isEmpty()) {
					int rowIndexForList = rowIndex;
					HSSFRow rowForList;
					for (int i = 0; i < listProps.size(); i++) {
						if (sheet.getRow(rowIndexForList) != null) {
							rowForList = sheet.getRow(rowIndexForList);
						}
						else {
							rowForList = sheet.createRow(rowIndexForList);
						}
						HSSFCell cell = rowForList.createCell(cellIndex);
						new SpelColumnRender<T>(
								expString + "[" + i + "]." + column.getListPropsExp())
								.render(cell, data);
						rowIndexForList++;
					}
				}
				else if (mergeRowCount == 1) {
					HSSFCell cell = row.createCell(cellIndex);
					new SpelColumnRender<T>(expString).render(cell, data);
				}
				else {
					CellRangeAddress cra = new CellRangeAddress(rowIndex,
							rowIndex + mergeRowCount - 1, cellIndex, cellIndex);
					sheet.addMergedRegion(cra);
					HSSFCell cell = row.createCell(cellIndex);
					new SpelColumnRender<T>(expString).render(cell, data);
					hasMerged = true;
				}
				cellIndex++;
			}
			if (hasMerged) {
				rowIndex = rowIndex + mergeRowCount;
			}
			else {
				rowIndex++;
			}
		}
	}

	private List getListProps(String listExp, T data) {
		ExpressionParser parser = new SpelExpressionParser();
		try {
			Expression exp = parser.parseExpression(listExp);
			Object o = exp.getValue(data);
			Class<?> aClass = o.getClass();
			if (aClass.isAssignableFrom(ArrayList.class)) {
				return (ArrayList) o;
			}
		}
		catch (SpelEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param op
	 */
	public void write(OutputStream op) {
		try {
			this.work.write(op);
		}
		catch (IOException e) {
			throw new SbcRuntimeException(e);
		}
	}

	public static void setError(Workbook workbook, Cell cell, String comment) {
		//如果未设红背景
		if (IndexedColors.RED.getIndex() != cell.getCellStyle()
				.getFillBackgroundColor()) {
			CellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(IndexedColors.RED.getIndex());
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cell.setCellStyle(style);
		}

		int maxRowIndex = workbook.getSheetAt(0).getLastRowNum();
		int maxCellIndex = cell.getRow().getLastCellNum();

		//当前单元格索引起始值
		int beginRowIndex = cell.getRowIndex() + 1;
		int beginCelIndex = cell.getColumnIndex() + 1;

		//范围性单元格终止值
		int endRowIndex = beginRowIndex + 2;
		int endCelIndex = beginCelIndex + 2;

		//如果终止值超出范围，新增行和列
		if (endRowIndex > maxRowIndex) {
			workbook.getSheetAt(0).createRow(endRowIndex);
		}
		if (endCelIndex > maxCellIndex) {
			cell.getRow().createCell(endCelIndex);
		}

		Comment t_comment = cell.getCellComment();
		if (t_comment == null) {
			Drawing patriarch = workbook.getSheetAt(0).createDrawingPatriarch();
			t_comment = patriarch.createCellComment(patriarch
					.createAnchor(0, 0, 0, 0, (short) beginCelIndex, beginRowIndex,
							(short) endCelIndex, endRowIndex));
		}

		RichTextString richTextString = t_comment.getString();
		String oldComment = (richTextString == null || StringUtils
				.isEmpty(richTextString.getString())) ?
				comment :
				richTextString.getString().concat("\n").concat(comment);
		//2003的批注
		if (richTextString instanceof HSSFRichTextString) {
			t_comment.setString(new HSSFRichTextString(oldComment));
		}
		else {
			t_comment.setString(new XSSFRichTextString(oldComment));
		}
		cell.setCellComment(t_comment);
	}

	/**
	 * 获取值
	 */
	public static String getValue(Cell cell) {
		if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			// 返回布尔类型的值
			return ObjectUtils.toString(cell.getBooleanCellValue()).trim();
		}
		else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			// 返回数值类型的值
			return ObjectUtils.toString(BigDecimal.valueOf(cell.getNumericCellValue()))
					.trim();
		}
		else {
			// 返回字符串类型的值
			return ObjectUtils.toString(cell.getStringCellValue()).trim();
		}
	}

	/**
	 * 设置下拉列表元素
	 *
	 * @param strFormula 区域值 如sheet2!$A$1:$A$53
	 * @param firstRow   起始行
	 * @param endRow     终止行
	 * @param firstCol   起始列
	 * @param endCol     终止列
	 * @return HSSFDataValidation
	 * @throws
	 */
	public static HSSFDataValidation getDataValidation(String strFormula, int firstRow,
			int endRow, int firstCol, int endCol) {

		// 设置数据有效性加载在哪个单元格上。四个参数分别是：起始行、终止行、起始列、终止列
		CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow,
				firstCol, endCol);
		DVConstraint constraint = DVConstraint.createFormulaListConstraint(strFormula);
		HSSFDataValidation dataValidation = new HSSFDataValidation(regions, constraint);
		dataValidation.createErrorBox("Error", "Error");
		dataValidation.createPromptBox("", null);
		return dataValidation;
	}

	/**
	 * 通用导出方法
	 * @param title 文件标题
	 * @param headColumnNames  头部列的名称 ['某xxx','某xxx2']
	 * @param headColumnExp  头部列的字段表达式 ['id','name']
	 * @param dataRecords 数据list
	 * @param response 框架响应对象，可以设置头信息
	 */
	public static <T,O> void export(String title, List<String> headColumnNames, List<String> headColumnExp, List<T> dataRecords, O response) {
		try{
			ExcelHelper excelHelper = new ExcelHelper();
			List<Column> columnList = new ArrayList<>();
			AtomicInteger colNameNum = new AtomicInteger(0);
			headColumnNames.forEach(m -> {
				int index = headColumnNames.indexOf(m);
				columnList.add(new Column(headColumnNames.get(index), new SpelColumnRender<T>(headColumnExp.get(index))));
				colNameNum.getAndIncrement();
			});
			String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
			String fileName = URLEncoder.encode(String.format(title+"_%s.xls", nowStr), "UTF-8");
			if(response instanceof HttpServletResponse){
				((HttpServletResponse)response).setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
				Column[] columns = columnList.toArray(new Column[columnList.size()]);
				excelHelper.addSheet(title, columns, dataRecords);
				excelHelper.write(((HttpServletResponse) response).getOutputStream());
			}else{
				new IllegalArgumentException("暂不支持该响应参数");
			}
		}catch (Exception e){
			log.error(e.getMessage(),e);
		}
	}
}
