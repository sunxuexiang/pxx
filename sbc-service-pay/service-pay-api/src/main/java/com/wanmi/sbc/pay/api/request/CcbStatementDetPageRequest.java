package com.wanmi.sbc.pay.api.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 建行对账单分账明细分页查询请求类
 * @author hudong
 * 2023-09-19
 */
@Data
@ApiModel
public class CcbStatementDetPageRequest extends BaseQueryRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 支付流水号
     */
    @ApiModelProperty(value = "支付流水号")
    private String pyOrdrNo;

    /**
     * 主订单编号
     */
    @ApiModelProperty(value = "主订单编号")
    private String mainOrdrNo;

    /**
     * 分账账户
     */
    @ApiModelProperty(value = "分账账户")
    private String rcvprtMktMrchId;

    /**
     * 分账日期
     */
    @ApiModelProperty(value = "分账日期")
    private Date clrgDt;

}
