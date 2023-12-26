package com.wanmi.sbc.order.request;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * <p>订单商品购买请求结构</p>
 * Created by of628-wenzhi on 2017-07-13-上午9:20.
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
public class TradeItemRequest extends BaseRequest {

    private static final long serialVersionUID = 5109763872537870011L;

    /**
     * skuId
     */
    @ApiModelProperty(value = "skuId")
    @NotBlank
    private String skuId;


    /**
     * devanningskuId
     */
    @ApiModelProperty(value = "devanningskuId")
    private long devanningskuId;

    @ApiModelProperty(value = "映射表伪spuid")
    private String parentGoodsInfoId;

    /**
     * 购买数量
     */
    @ApiModelProperty(value = "购买数量")
    @Range(min = 1)
    private long num;

    /**
     * 购买商品的价格
     */
    @ApiModelProperty(value = "购买商品的价格")
    private BigDecimal price;

    /**
     * 是否是秒杀抢购商品
     */
    private Boolean isFlashSaleGoods;

    /**
     * 秒杀抢购商品Id
     */
    private Long flashSaleGoodsId;

    /**
     * erp的编码
     */
    private String erpSkuNo;

    /**
     * 步长
     */
    private BigDecimal addStep;

}
