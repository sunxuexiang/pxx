package com.wanmi.sbc.returnorder.api.response.trade;

import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-05 9:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TradeAddWithOpResponse implements Serializable {

    /**
     * 交易单
     */
    @ApiModelProperty(value = "交易单")
    private TradeVO tradeVO;
}
