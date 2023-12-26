package com.wanmi.sbc.pay.api.request;

import lombok.Data;

@Data
public class CmbDoBusinessRepDataResponse {

    private String rspMsg;

    private String fbPubKey;

    private String dateTime;

    private String rspCode;
}
