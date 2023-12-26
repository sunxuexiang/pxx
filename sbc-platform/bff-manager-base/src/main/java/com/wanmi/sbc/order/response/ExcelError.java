package com.wanmi.sbc.order.response;

import lombok.Data;

@Data
public class ExcelError {

    private String code;

    private String message;

    public ExcelError(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
