package com.wanmi.sbc.wms.requestwms.model;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName: RequestSysetemParam
 * @Description: 系统级输入参数，除去data
 * @Author: lh
 * @Date: 2020/5/7 16:28
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
public class WMSRequestSystemParam implements Serializable {
    private static final long serialVersionUID = 5121152846925843891L;
    private String method;//请求方法
    private String clientDB;
    private String clientCustomerID;
    private String messageID;
    private String appToken;
    private String appKey;
    private String sign;
    private String timeStamp;
}
