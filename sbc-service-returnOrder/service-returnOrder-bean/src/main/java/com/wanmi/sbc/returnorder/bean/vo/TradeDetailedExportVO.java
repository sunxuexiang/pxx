package com.wanmi.sbc.returnorder.bean.vo;

import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.marketing.bean.vo.TradeCouponVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description:
 * @Autho qiaokang
 * @Date：2020-03-30 10:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class TradeDetailedExportVO implements Serializable {

    private static final long serialVersionUID = 4696700071307158253L;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String parentId;

    @ApiModelProperty(value = "活动类型(3.新提货 4.新囤货)")
    private String activityType;

    /**
     * 子订单号
     */
    @ApiModelProperty(value = "子订单号")
    private String id;

    /**
     * 订单总体状态
     */
    @ApiModelProperty(value = "订单总体状态")
    private TradeStateVO tradeState;

    /**
     * 购买人
     */
    @ApiModelProperty(value = "购买人")
    private BuyerVO buyer;

    /**
     * 收货人信息
     */
    @ApiModelProperty(value = "收货人信息")
    private ConsigneeVO consignee;

    @ApiModelProperty(value = "支付信息")
    private PayInfoVO payInfo;

    /**
     * 订单价格
     */
    @ApiModelProperty(value = "订单价格")
    private TradePriceVO tradePrice;

    /**
     * 订单使用的店铺优惠券
     */
    @ApiModelProperty(value = "订单使用的店铺优惠券")
    private TradeCouponVO tradeCoupon;

    /**
     * 买家备注
     */
    @ApiModelProperty(value = "买家备注")
    private String buyerRemark;

    /**
     * 卖家备注
     */
    @ApiModelProperty(value = "卖家备注")
    private String sellerRemark;

    /**
     * 业务员名称
     */
    @ApiModelProperty(value = "业务员名称")
    private String employeeName;

    /**
     * 白鲸管家名称
     */
    @ApiModelProperty(value = "白鲸管家名称")
    private String managerName;

    /**
     * 入驻商家代表
     */
    @ApiModelProperty(value = "入驻商家代表")
    private String investmentManager;

    /**
     * 仓库名称
     */
    private String wareName;

    /**
     * 发票
     */
    @ApiModelProperty(value = "发票")
    private InvoiceVO invoice;

    /**
     * 配送方式
     */
    @ApiModelProperty(value = "配送方式")
    private DeliverWay deliverWay;

    @ApiModelProperty(value = "skuNo")
    private String skuNo;

    @ApiModelProperty(value = "erpGoodsInfoNo")
    private String erpGoodsInfoNo;

    @ApiModelProperty(value = "skuName")
    private String skuName;

    /**
     * 商品副标题
     */
    @ApiModelProperty(value = "商品fu标题")
    private String goodsSubtitle;


    /**
     * 购买数量
     */
    @ApiModelProperty(value = "购买数量")
    private Long num;


    /**
     * 成交价格
     */
    @ApiModelProperty(value = "成交价格")
    private BigDecimal price;

    /**
     * 平摊小计 - 最初由 levelPrice*num（购买数量） 计算
     */
    @ApiModelProperty(value = "平摊小计")
    private BigDecimal splitPrice;

    /**
     * 商品价格 - 会员价 & 阶梯设价
     */
    @ApiModelProperty(value = "商品价格")
    private BigDecimal levelPrice;

    /**
     * 商品成本价
     */
    @ApiModelProperty(value = "商品成本价")
    private BigDecimal costPrice;

    @ApiModelProperty(value = "实付金额")
    private BigDecimal actualPrice;

    private String supplierName;

    private String storeName;

    /**
     * 支付单号
     */
    @ApiModelProperty(value = "支付单号")
    private String payOrderNo;

    /**
     * 库位名称
     */
    @ApiModelProperty(value = "库位名称")
    private String stockName;

    /**
     * 订单返鲸币总数量
     */
    @ApiModelProperty(value = "订单返鲸币总数量")
    private BigDecimal returnCoin;

    /**
     * 二维码
     */
    @ApiModelProperty(value = "二维码")
    private String goodsInfoQrcode;

    /**
     * 商品条形码
     */
    @ApiModelProperty(value = "商品条形码")
    private String goodsInfoBarcode;
}
