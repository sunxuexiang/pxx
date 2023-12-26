package com.wanmi.sbc.pay.api.request;

import lombok.Data;

@Data
public class CmbDoBusinessRequest {
    private String version;

    private String charset;

    private String signType;

    private String sign;

    private CmbReqDataRequest reqData;

}
