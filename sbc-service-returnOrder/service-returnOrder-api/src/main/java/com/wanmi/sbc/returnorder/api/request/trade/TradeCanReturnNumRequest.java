package com.wanmi.sbc.returnorder.api.request.trade;

import com.wanmi.sbc.returnorder.bean.dto.TradeDTO;
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
 * @Date: 2018-12-05 15:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TradeCanReturnNumRequest implements Serializable {


    private static final long serialVersionUID = 3332350082446025482L;
    /**
     * 交易单
     */
    @ApiModelProperty(value = "交易单")
    private TradeDTO trade;


}
