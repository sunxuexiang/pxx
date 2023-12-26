package com.wanmi.sbc.order.util;
//
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ExportExcelUtil<T extends BaseRowModel> {

    public ExportExcelUtil() {
    }

    public void createExcel(ByteArrayOutputStream out, List<T> data, List<String> tableHeadList) throws IOException {
        try {
            List<List<String>> head = getExcelHead(tableHeadList);

            ExcelWriter writer = new ExcelWriter(null, out, ExcelTypeEnum.XLSX, true);
            Table table = new Table(0);
            table.setHead(head);
            Sheet sheet1 = new Sheet(1, 0);
            sheet1.setAutoWidth(true);
            sheet1.setSheetName("sheet1");
            writer.write(data, sheet1, table);
            writer.finish();
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private List<List<String>> getExcelHead(List<String> tableHeadList){
        List<List<String>> head = new ArrayList<>();
        for (String s : tableHeadList) {
            List<String> column = new ArrayList<String>();
            column.add(s);
            head.add(column);
        }
        return head;
    }

}