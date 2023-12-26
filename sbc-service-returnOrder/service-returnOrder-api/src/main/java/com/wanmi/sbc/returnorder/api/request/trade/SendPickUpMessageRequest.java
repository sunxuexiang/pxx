package com.wanmi.sbc.returnorder.api.request.trade;

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
 * @Date: 2018-12-05 15:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class SendPickUpMessageRequest implements Serializable {

    private static final long serialVersionUID = -6081312432346193605L;
    /**
     * 交易id
     */
    @ApiModelProperty(value = "交易id")
    private TradeVO trade;
}
