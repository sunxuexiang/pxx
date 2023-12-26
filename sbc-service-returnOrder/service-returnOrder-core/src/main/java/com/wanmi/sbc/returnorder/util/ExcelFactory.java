package com.wanmi.sbc.returnorder.util;

import com.alibaba.excel.metadata.BaseRowModel;
import org.springframework.stereotype.Component;

@Component
public class ExcelFactory<T extends BaseRowModel> {
    public ExportExcelUtil<T> createExportExcel() {
        return new ExportExcelUtil<>();
    }
}
