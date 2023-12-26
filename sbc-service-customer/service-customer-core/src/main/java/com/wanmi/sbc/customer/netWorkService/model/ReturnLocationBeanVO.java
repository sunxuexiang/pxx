package com.wanmi.sbc.customer.netWorkService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnLocationBeanVO implements Serializable {
    private static final long serialVersionUID = -1755280482491292875L;
    private int status;//返回结果状态值， 成功返回0，其他值请查看下方返回码状态表。

    private RetrunLocationResultBeanVO result;

}
