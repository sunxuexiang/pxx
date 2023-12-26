package com.wanmi.sbc.order.request;

import com.wanmi.sbc.order.bean.dto.NewPileTradeQueryDTO;
import io.swagger.annotations.ApiModelProperty;

/**
 * 新囤货订单导出列表参数结构
 * @author lm
 * @date 2022/10/10 9:41
 */
public class NewPileTradeExportRequest extends NewPileTradeQueryDTO {

    @ApiModelProperty(value = "jwt token")
    private String token;
}
