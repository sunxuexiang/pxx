package com.wanmi.sbc.marketing.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: gaomuwei
 * @Date: Created In 上午10:39 2018/10/8
 * @Description:订单营销插件响应类
 */
@ApiModel
@Data
public class TradeMarketingWrapperTryCatchVO implements Serializable {


    private static final long serialVersionUID = 1288383221632080447L;

    @ApiModelProperty(value = "订单营销插件响应类")
    List<TradeMarketingWrapperVO> tradeMarketingWrapperVOS;

    @ApiModelProperty(value = "不满足的营销id")
    List<Long> marketingIds;
}
