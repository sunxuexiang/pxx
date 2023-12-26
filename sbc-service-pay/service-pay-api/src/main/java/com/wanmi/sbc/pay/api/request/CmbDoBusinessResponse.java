package com.wanmi.sbc.pay.api.request;

import lombok.Data;

@Data
public class CmbDoBusinessResponse {

    private String version;

    private String charset;

    private String sign;

    private String signType;

    private CmbDoBusinessRepDataResponse rspData;
}
