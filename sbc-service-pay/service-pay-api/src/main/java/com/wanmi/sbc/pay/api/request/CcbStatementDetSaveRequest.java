package com.wanmi.sbc.pay.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 建行对账单分账明细保存请求类
 * @author hudong
 * 2023-09-19
 */
@Data
@ApiModel
public class CcbStatementDetSaveRequest {

    /**
     * 对账单分账明细数据集
     */
    @ApiModelProperty(value = "对账单分账明细数据集")
    private List<CcbStatementDetRequest> ccbStatementDetRequestList;
}
