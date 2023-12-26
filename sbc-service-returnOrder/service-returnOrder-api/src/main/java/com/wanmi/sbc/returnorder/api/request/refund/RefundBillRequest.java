package com.wanmi.sbc.returnorder.api.request.refund;

import com.wanmi.sbc.returnorder.bean.dto.RefundBillDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 流水单
 * Created by zhangjin on 2017/4/21.
 */
@Data
@ApiModel
public class RefundBillRequest extends RefundBillDTO implements Serializable{

    private static final long serialVersionUID = -5819780428877519731L;

    @ApiModelProperty(value = "退款单编号")
    private String returnOrderCode;
}
