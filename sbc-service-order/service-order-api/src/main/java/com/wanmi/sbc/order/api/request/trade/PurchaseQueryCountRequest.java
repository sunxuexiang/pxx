package com.wanmi.sbc.order.api.request.trade;

import com.wanmi.sbc.order.bean.dto.TradeQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>描述<p>
 * 查询采购总数
 * @author zhaowei
 * @date 2021/4/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class PurchaseQueryCountRequest implements Serializable {

    /**
     * 查询参数
     */
    @ApiModelProperty(value = "参数")
    private TradeQueryDTO tradePageDTO;

}
