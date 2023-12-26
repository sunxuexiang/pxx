package com.wanmi.sbc.customer.employee.model.root;

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
@Getter
@Setter
public class KingdeeErpModel implements Serializable {
    private static final long serialVersionUID = 3073092342237220503L;

    /**
     * erp系统id
     */
    @JSONField(name = "FSalerId")
    private String erpEmployeeId;
}
