package com.wanmi.sbc.returnorder.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 退货商品类目
 * Created by jinwei on 19/4/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class ReturnItemDTO implements Serializable {

    private static final long serialVersionUID = -6299234989806779463L;

    @ApiModelProperty(value = "skuId")
    private String skuId;

    @ApiModelProperty(value = "sku 名称")
    private String skuName;

    @ApiModelProperty(value = "sku 编号")
    private String skuNo;

    @ApiModelProperty(value = "devanningId")
    private Long devanningId;

    /**
     * 规格信息
     */
    @ApiModelProperty(value = "规格信息")
    private String specDetails;

    /**
     * 退货商品单价 = 商品原单价 - 商品优惠单价
     */
    @ApiModelProperty(value = "退货商品单价 = 商品原单价 - 商品优惠单价")
    private BigDecimal price;

    /**
     * 平摊价格
     */
    @ApiModelProperty(value = "平摊价格")
    private BigDecimal splitPrice;

    /**
     * 供货价
     */
    @ApiModelProperty(value = "供货价")
    private BigDecimal supplyPrice;

    /**
     * 供货价小计
     */
    @ApiModelProperty(value = "供货价小计")
    private BigDecimal providerPrice;

    /**
     * 订单平摊价格
     */
    @ApiModelProperty(value = "订单平摊价格")
    private BigDecimal orderSplitPrice;

    /**
     * 余额支付金额
     */
    private BigDecimal balancePrice;

    /**
     * 申请退货数量
     */
    @ApiModelProperty(value = "申请退货数量")
    private BigDecimal num;

    /**
     * 退货商品图片路径
     */
    @ApiModelProperty(value = "退货商品图片路径")
    private String pic;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 仍可退数量
     */
    @ApiModelProperty(value = "仍可退数量")
    private Integer canReturnNum;

    /**
     * erpSkuNo
     */
    @ApiModelProperty(value = "erpSkuNo")
    private String erpSkuNo;

    /**
     * 步长
     */
    private BigDecimal addStep;

    /**
     * 实际收货数量
     */
    private String receivedQty;

    /**
     * 批次号
     */
    private String goodsBatchNo;

    /**
     * 商品副标题
     */
    @ApiModelProperty(value = "商品副标题")
    private String goodsSubtitle;

    /**
     * 每个商品的价格
     */
    private List<InventoryDetailSamountTradeDTO> inventoryDetailSamountTrades = new ArrayList<>();

    /**
     * 每个退货商品来源囤货单信息
     */
    private List<PickGoodsDTO> returnGoodsList;
}
