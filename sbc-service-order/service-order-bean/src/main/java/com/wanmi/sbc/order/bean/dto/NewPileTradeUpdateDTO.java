package com.wanmi.sbc.order.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.util.CustomLocalDateDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.marketing.bean.vo.TradeCouponVO;
import com.wanmi.sbc.marketing.bean.vo.TradeMarketingVO;
import com.wanmi.sbc.order.bean.enums.EvaluateStatus;
import com.wanmi.sbc.order.bean.enums.OrderSource;
import com.wanmi.sbc.order.bean.enums.PaymentOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class NewPileTradeUpdateDTO implements Serializable {

    private static final long serialVersionUID = -6414564526969459575L;

    /**
     * 订单号
     */
    @Id
    @ApiModelProperty(value = "订单号")
    private String id;

    /**
     * 父订单号
     */
    @ApiModelProperty(value = "父订单号")
    private String parentId;

    /**
     * 订单组号
     */
    @ApiModelProperty(value = "订单组号")
    private String groupId;

    /**
     * 购买人
     */
    @ApiModelProperty(value = "购买人")
    private BuyerDTO buyer;

    /**
     * boss卖方
     */
    @ApiModelProperty(value = "boss卖方")
    private SellerDTO seller;

    /**
     * 商家
     */
    @ApiModelProperty(value = "商家")
    private SupplierDTO supplier;

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
     * 订单附件，以逗号隔开
     */
    @ApiModelProperty(value = "订单附件,以逗号隔开")
    private String encloses;

    /**
     * 调用方的请求 IP
     * added by shenchunnan
     */
    @ApiModelProperty(value = "调用方的请求 IP")
    private String requestIp;

    /**
     * 发票
     */
    @ApiModelProperty(value = "发票")
    private InvoiceDTO invoice;

    /**
     * 订单总体状态
     */
    @ApiModelProperty(value = "订单总体状态")
    private NewPileTradeStateDTO tradeState;

    /**
     * 收货人信息
     */
    @ApiModelProperty(value = "收货人信息")
    private ConsigneeDTO consignee;

    /**
     * 订单价格
     */
    @ApiModelProperty(value = "订单价格")
    private TradePriceDTO tradePrice;

    /**
     * 订单商品列表
     */
    @ApiModelProperty(value = "订单商品列表")
    private List<TradeItemDTO> tradeItems = new ArrayList<>();

    /**
     * 发货单
     */
    @ApiModelProperty(value = "发货单")
    private List<TradeDeliverDTO> tradeDelivers = new ArrayList<>();

    /**
     * 配送方式
     */
    @ApiModelProperty(value = "配送方式")
    private DeliverWay deliverWay;

    @ApiModelProperty(value = "物流公司信息(如果配送方式是物流则存在)")
    private LogisticsInfoDTO logisticsCompanyInfo;

    @ApiModelProperty(value = "订单支付信息")
    private PayInfoDTO payInfo;

    /**
     * 支付单ID
     */
    @ApiModelProperty(value = "支付单ID")
    private String payOrderId;

    /**
     * 订单来源方
     */
    @ApiModelProperty(value = "订单来源方")
    private Platform platform;

    /**
     * 下单时是否已开启订单自动审核
     */
    @ApiModelProperty(value = "下单时是否已开启订单自动审核",dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private Boolean isAuditOpen = true;

    /**
     * 订单支付顺序
     */
    @ApiModelProperty(value = "订单支付顺序")
    private PaymentOrder paymentOrder;

    /**
     * 超时未支付取消订单时间
     */
    @ApiModelProperty(value = "超时未支付取消订单时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime orderTimeOut;

    /**
     * 操作日志记录（状态变更）
     */
    @ApiModelProperty(value = "操作日志记录", notes = "状态变更")
    private List<TradeEventLogDTO> tradeEventLogs = new ArrayList<>();

    /**
     * 是否被结算
     */
    @ApiModelProperty(value = "是否被结算",dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private Boolean hasBeanSettled;

    /**
     * 是否可退标识
     */
    @ApiModelProperty(value = "是否可退标识",dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private Boolean canReturnFlag;

    /**
     * 退款标识
     * 仅供结算使用 - 标识该订单是未收货的退款单子
     * <p>
     * 该单子flowState是作废不会入账，但是退单是COMPLETE状态会入账，导致收支不公，加了单独的状态作为判断
     */
    @ApiModelProperty(value = "退款标识", dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private Boolean refundFlag;

    /**
     * 订单营销信息
     */
    @ApiModelProperty(value = "订单营销信息")
    private List<TradeMarketingVO> tradeMarketings;

    /**
     * 订单使用的店铺优惠券
     */
    @ApiModelProperty(value = "订单使用的店铺优惠券")
    private TradeCouponVO tradeCoupon;


    /**
     * 选择的自提点信息
     */
    @ApiModelProperty(value = "选择的自提点信息")
    private TradeWareHouseDTO tradeWareHouse;

    /**
     * 营销赠品全量列表
     */
    @ApiModelProperty(value = "营销赠品全量列表")
    private List<TradeItemDTO> gifts = new ArrayList<>();

    /**
     * 订单来源--区分h5,pc,app,小程序,代客下单
     */
    @ApiModelProperty(value = "订单来源")
    private OrderSource orderSource;

    /**
     * 是否拼团订单
     */
    @ApiModelProperty(value = "是否拼团订单")
    private boolean grouponFlag;

    /**
     * 订单拼团信息
     */
    @ApiModelProperty(value = "订单拼团信息")
    private TradeGrouponDTO tradeGroupon;


    /**
     * 分销渠道类型
     */
    @ApiModelProperty(value = "分销渠道类型")
    private ChannelType channelType;

    /**
     * 小B店铺内分享链接携带的邀请人ID（会员ID）
     */
    private String distributionShareCustomerId;

    /**
     * 返利人分销员id
     */
    @ApiModelProperty(value = "返利人分销员id")
    private String distributorId;

    /**
     * 返利人会员id
     */
    @ApiModelProperty(value = "返利人会员id")
    private String inviteeId;

    /**
     * 小店名称
     */
    @ApiModelProperty(value = "小店名称")
    private String shopName;

    /**
     * 返利人名称
     */
    @ApiModelProperty(value = "返利人名称")
    private String distributorName;

    /**
     * 开店礼包
     */
    @ApiModelProperty(value = "开店礼包")
    private DefaultFlag storeBagsFlag;

    /**
     * 开店礼包邀请人id
     */
    @ApiModelProperty(value = "开店礼包邀请人id")
    private String storeBagsInviteeId;

    /**
     * 分销单品列表
     */
    @ApiModelProperty(value = "分销单品列表")
    private List<TradeDistributeItemDTO> distributeItems = new ArrayList<>();

    /**
     * 返利人佣金
     */
    @ApiModelProperty(value = "返利人佣金")
    private BigDecimal commission;

    /**
     * 总佣金(返利人佣金 + 提成人佣金)
     */
    @ApiModelProperty(value = "总佣金(返利人佣金 + 提成人佣金)")
    private BigDecimal totalCommission;

    /**
     * 提成人佣金列表
     */
    @ApiModelProperty(value = "提成人佣金列表")
    private List<TradeCommissionDTO> commissions = new ArrayList<>();

    /**
     * 是否返利
     */
    @ApiModelProperty(value = "是否返利")
    private Boolean commissionFlag;

    /**
     * 正在进行的退单数量
     */
    @ApiModelProperty(value = "正在进行的退单数量")
    private Integer returnOrderNum;

    /**
     * 订单评价状态
     */
    @ApiModelProperty("订单评价状态")
    private EvaluateStatus orderEvaluateStatus;

    /**
     * 店铺服务评价状态
     */
    @ApiModelProperty(value = "店铺服务评价状态")
    private EvaluateStatus storeEvaluate;

    /**
     * 支付方式
     */
    @ApiModelProperty(value = "支付方式")
    private PayWay payWay;

    /**
     * 可退积分
     */
    @ApiModelProperty(value = "可退积分")
    private Long canReturnPoints;

    /**
     * 已退金额
     */
    @ApiModelProperty(value = "已退金额")
    private BigDecimal canReturnPrice;

    /**
     * 订单类型 0：普通订单；1：积分订单；
     */
    @ApiModelProperty(value = "订单类型 0：普通订单；1：积分订单")
    private OrderType orderType;

    /**
     * 积分订单类型 0：积分商品 1：积分优惠券
     */
    @ApiModelProperty(value = "积分订单类型 0：积分商品 1：积分优惠券")
    private PointsOrderType pointsOrderType;

    /**
     * 分享人id
     */
    @ApiModelProperty(value = "分享人id")
    private String shareUserId;

    /**
     * 是否是秒杀抢购商品订单
     */
    @ApiModelProperty(value = "是否是秒杀抢购商品订单")
    private Boolean isFlashSaleGoods;

    /**
     * 所属商户id-供应商使用
     */
    private Long storeId;

    /**
     * 所属商户名称-供应商使用
     */
    private String supplierName;

    /**
     * 所属商户编号-供应商使用
     */
    private String supplierCode;

    /**
     * 仓库编号
     */
    private String wareHouseCode;

    /**
     * 仓库编号
     */
    private Long wareId;


    /**
     *wms 第三方订单推送标志位
     */
    private Boolean WMSPushFlag;


    /**
     * 预约发货时间
     */
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate bookingDate;

    /**
     * 活动类型
     */
    private String activityType;

    /**
     * 物流 第三方物流(有地址)：01 超级大白金待选物流(无地址)：02
     */
    private String logistics;

    /**
     * 套装数
     */
    private Long suitBuyCount;

    /**
     * 套装id
     */
    private Long marketingId;

    /**
     * 销售类型 0批发 1销售
     */
    @ApiModelProperty(value = "销售类型 0批发 1零售")
    private SaleType saleType;

    /**
     * 购买指定商品赠券codeId集合
     */
    @ApiModelProperty(value = "购买指定商品赠券codeId集合")
    private List<String> sendCouponCodeIds;

    /**
     * 购买指定商品赠券关联skuIds
     */
    @ApiModelProperty(value = "购买指定商品赠券关联skuIds")
    private List<String> couponSkuIds;

    /**
     * 展示乡镇件标识:默认false
     */
    @ApiModelProperty(value = "展示乡镇件标识:默认false")
    private Boolean newVilageFlag = false;

    /**
     * 支付订单号
     */
    private String payOrderNo;

}
