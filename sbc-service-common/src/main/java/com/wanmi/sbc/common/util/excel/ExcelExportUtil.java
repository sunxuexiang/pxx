package com.wanmi.sbc.common.util.excel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author shiGuangYi
 * @createDate 2023-08-02 10:38
 * @Description: 导出方法
 * @Version 1.0
 */
public class ExcelExportUtil {
    private static final int BATCH_SIZE = 100000; // 每批次导出的数据量
    private static final int MAX_THREAD_NUM = Runtime.getRuntime().availableProcessors(); // 获取CPU核数
    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(MAX_THREAD_NUM); // 创建线程池

    /**
     * 导出Excel文件
     *
     * @param dataList 数据列表
     * @param file     文件路径
     */
    public static void exportExcel(List<List<Object>> dataList, String file) throws Exception {
        Workbook workbook = new SXSSFWorkbook(); // 创建工作簿
        Sheet sheet = workbook.createSheet(); // 创建工作表
        int rowNumber = 0; // 行号

        for (int i = 0; i < dataList.size(); i += BATCH_SIZE) {
            List<List<Object>> subList = dataList.subList(i, Math.min(i + BATCH_SIZE, dataList.size())); // 分批次处理数据
            List<Callable<ExportTask>> tasks = new ArrayList<>();
            for (List<Object> rowData : subList) {
                int finalRowNumber = rowNumber;
                Callable<ExportTask> exportTaskCallable= () -> {
                    ExportTask exportTask = new ExportTask(rowData, sheet.createRow(finalRowNumber));// 创建导出任务
                    return exportTask;
                };
                tasks.add(exportTaskCallable);
                rowNumber++;
            }
            THREAD_POOL.invokeAll(tasks); // 执行导出任务
        }
        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream); // 写入文件
        outputStream.close();
        workbook.close();
    }

    /**
     * 导出任务
     */
    private static class ExportTask implements Runnable {
        private List<Object> rowData; // 数据行
        private Row row; // 工作表行

        public ExportTask(List<Object> rowData, Row row) {
            this.rowData = rowData;
            this.row = row;
        }

        @Override
        public void run() {
            for (int i = 0; i < rowData.size(); i++) {
                Cell cell = row.createCell(i);
                Object value = rowData.get(i);

                if (value instanceof String) {
                    cell.setCellValue((String) value);
                } else if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else if (value instanceof Boolean) {
                    cell.setCellValue((Boolean) value);
                } else if (value instanceof java.util.Date) {
                    cell.setCellValue((java.util.Date) value);
                } else {
                    cell.setCellValue(value == null ? "" : value.toString());
                }
            }
        }
    }
}