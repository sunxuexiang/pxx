package com.wanmi.sbc.pay.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 建行对账单明细保存请求类
 * @author hudong
 * 2023-09-04
 */
@Data
@ApiModel
public class CcbStatementSumSaveRequest {

    /**
     * 对账单汇总数据集
     */
    @ApiModelProperty(value = "对账单汇总数据集")
    private List<CcbStatementSumRequest> ccbStatementSumRequestList;
}
