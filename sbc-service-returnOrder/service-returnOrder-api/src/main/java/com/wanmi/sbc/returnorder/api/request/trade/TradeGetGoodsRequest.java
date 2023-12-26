package com.wanmi.sbc.returnorder.api.request.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-04 17:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class TradeGetGoodsRequest implements Serializable {

    /**
     *
     */
    @ApiModelProperty(value = "skuIds")
    private List<String> skuIds;

    @ApiModelProperty(value = "devanningIds")
    private List<Long> devanningIds;

    /**
     * 仓库Id
     */
    private Long wareId;

    /**
     * 仓库的编码
     */
    private String wareHouseCode;

    /**
     * 是否匹配到仓
     */
    Boolean matchWareHouseFlag;
}
