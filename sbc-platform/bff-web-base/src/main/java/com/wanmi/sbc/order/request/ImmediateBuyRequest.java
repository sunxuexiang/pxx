package com.wanmi.sbc.order.request;

import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: gaomuwei
 * @Date: Created In 下午3:28 2019/3/6
 * @Description:
 */
@ApiModel
@Data
public class ImmediateBuyRequest  implements Serializable {

    private static final long serialVersionUID = 1837708693840925881L;
    /**
     * 商品skuId
     */
    @ApiModelProperty("商品skuId")
    @NotNull
    private String skuId;

    /**
     * 商品skuId
     */
    @ApiModelProperty("商品skuId")
    @NotNull(message = "商品id必传")
    private Long devanningId;

    /**
     * 购买数量
     */
    @ApiModelProperty("购买数量")
    @NotNull
    private Long num;

    /**
     * 是否开店礼包
     */
    @ApiModelProperty("是否开店礼包")
    private DefaultFlag storeBagsFlag;

    @ApiModelProperty("分仓ID")
    private Long wareId;

}
