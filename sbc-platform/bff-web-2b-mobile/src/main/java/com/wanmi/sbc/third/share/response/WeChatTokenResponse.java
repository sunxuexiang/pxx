package com.wanmi.sbc.third.share.response;

import lombok.Data;

@Data
public class WeChatTokenResponse {

    private Integer errcode;

    private String errmsg;

    private String access_token;

    private Integer expires_in;
}
