package com.wanmi.sbc.marketing.api.request.market;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-19 14:43
 */
@ApiModel
@Data
public class MarketingDeleteByIdRequest  extends MarketingIdRequest{

    private static final long serialVersionUID = 294888499089073088L;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private String operatorId;

}
