package com.wanmi.sbc.returnorder.api.request.trade;

import com.wanmi.sbc.returnorder.bean.dto.ReturnOrderDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>描述<p>
 * mq定时退单信息
 * @author zhaowei
 * @date 2021/4/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TradeOutRequest implements Serializable {

    /**
     * 缺货信息明细
     */
    @ApiModelProperty(value = "缺货信息明细")
    private ReturnOrderDTO returnOrderDTO;
}
