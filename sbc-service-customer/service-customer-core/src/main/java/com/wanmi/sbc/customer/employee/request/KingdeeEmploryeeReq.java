package com.wanmi.sbc.customer.employee.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class KingdeeEmploryeeReq implements Serializable {
    private static final long serialVersionUID = 7877801121804768358L;

    /**
     * 业务员姓名
     */
    @JSONField(name = "FName")
    private String emploryeeName;

    /**
     * 业务员手机号
     */
    @JSONField(name = "FTel")
    private String emploryeeMobile;
}
