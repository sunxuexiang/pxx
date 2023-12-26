package com.wanmi.sbc.order.api.request.discount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

/**
 * 优惠金额明细响应类
 * @author lm
 * @date 2022/12/09 10:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "优惠金额明细请求类")
public class DiscountPriceDetailRequest {

    @ApiModelProperty("商品名称")
    @NotEmpty(message = "商品名称不能为空")
    private String goodsInfoName;

    @ApiModelProperty("商品规格")
    @NotEmpty(message = "商品规格不能为空")
    private String goodsSubtitle;

    @ApiModelProperty("购买数量")
    @NotEmpty(message = "购买数量不能为空")
    @Min(value = 1,message = "购买数量不能为0")
    private Integer buyNum;

    @ApiModelProperty("商品金额")
    @NotEmpty(message = "商品金额不能为空")
    private BigDecimal totalPrice;

    @ApiModelProperty("商品图片")
    @NotEmpty(message = "商品图片不能为空")
    private String goodsImageUrl;
}
