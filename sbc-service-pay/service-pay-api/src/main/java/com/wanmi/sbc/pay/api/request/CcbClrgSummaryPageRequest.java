package com.wanmi.sbc.pay.api.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 建行对账单分账汇总分页查询请求类
 * @author hudong
 * 2023-09-23
 */
@Data
@ApiModel
public class CcbClrgSummaryPageRequest extends BaseQueryRequest {
    private static final long serialVersionUID = 1L;


    /**
     * 分账账户
     */
    @ApiModelProperty(value = "分账账户")
    private String mktMrchId;

    /**
     * 搜索条件:分账时间开始
     */
    @ApiModelProperty(value = "搜索条件:分账时间开始")
    private Date clrgDtBegin;
    /**
     * 搜索条件:分账时间截止
     */
    @ApiModelProperty(value = "搜索条件:分账时间截止")
    private Date clrgDtEnd;


}
