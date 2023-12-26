package com.wanmi.sbc.returnorder.trade.model.root;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.util.CustomLocalDateDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.customer.bean.vo.NetWorkVO;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.marketing.bean.vo.TradeCouponVO;
import com.wanmi.sbc.marketing.bean.vo.TradeMarketingVO;
import com.wanmi.sbc.returnorder.bean.enums.*;
import com.wanmi.sbc.returnorder.trade.model.entity.*;
import com.wanmi.sbc.returnorder.trade.model.entity.value.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 订单状态
 * Created by jinwei on 14/3/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trade implements Serializable {

    private static final long serialVersionUID = -6414564526969459575L;

    /**
     * 订单号
     */
    @Id
    private String id;

    /**
     * 父订单号，用于组织批量订单合并支付，目前仅在支付与退款中使用。
     */
    private String parentId;

    /**
     * 是否必须合并支付flag
     */
    private Boolean mergFlag =false;

    /**
     * 订单同级集合
     */
    private List<String> tids;

    /**
     * 订单组号
     */
    private String groupId;

    /**
     * 购买人
     */
    private Buyer buyer;

    /**
     * boss卖方
     */
    private Seller seller;

    /**
     * 商家
     */
    private Supplier supplier;

    /**
     * 买家备注
     */
    private String buyerRemark;

    /**
     * 卖家备注
     */
    private String sellerRemark;

    /**
     * 订单附件，以逗号隔开
     */
    private String encloses;

    /**
     * 调用方的请求 IP
     * added by shenchunnan
     */
    private String requestIp;

    /**
     * 发票
     */
    private Invoice invoice;

    /**
     * 订单总体状态
     */
    private TradeState tradeState;

    /**
     * 收货人信息
     */
    private Consignee consignee;

    /**
     * 订单价格
     */
    private TradePrice tradePrice;

    /**
     * 订单商品列表
     */
    private List<TradeItem> tradeItems = new ArrayList<>();

    /**
     * 积分订单优惠券
     */
    private TradePointsCouponItem tradeCouponItem;

    /**
     * 发货单
     */
    private List<TradeDeliver> tradeDelivers = new ArrayList<>();

    /**
     * 配送方式
     */
    private DeliverWay deliverWay;
    /**
     * 物流公司信息(如果配送方式是物流则存在)
     */
    private LogisticsInfo logisticsCompanyInfo;


    /**
     * 如果配送方式是战点自提
     */
    private NetWorkVO netWorkVO;


    private PayInfo payInfo;

    /**
     * 支付单ID
     */
    private String payOrderId;


    /**
     * 订单来源方
     */
    private Platform platform;

    /**
     * 下单时是否已开启订单自动审核
     */
    private Boolean isAuditOpen = true;

    /**
     * 订单支付顺序
     */
    private PaymentOrder paymentOrder;

    /**
     * 超时未支付取消订单时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime orderTimeOut;

    /**
     * 操作日志记录（状态变更）
     */
    private List<TradeEventLog> tradeEventLogs = new ArrayList<>();

    /**
     * 分销渠道类型
     */
    private ChannelType channelType;

    /**
     * 小B店铺内分享链接携带的邀请人ID（会员ID）
     */
    private String distributionShareCustomerId;

    /**
     * 返利人分销员id
     */
    private String distributorId;

    /**
     * 返利人会员id
     */
    private String inviteeId;

    /**
     * 小店名称
     */
    private String shopName;

    /**
     * 返利人名称
     */
    private String distributorName;

    /**
     * 开店礼包
     */
    private DefaultFlag storeBagsFlag = DefaultFlag.NO;

    /**
     * 开店礼包邀请人id
     */
    private String storeBagsInviteeId;

    /**
     * 分销单品列表
     */
    private List<TradeDistributeItem> distributeItems = new ArrayList<>();

    /**
     * 返利人佣金
     */
    private BigDecimal commission = BigDecimal.ZERO;

    /**
     * 总佣金(返利人佣金 + 提成人佣金)
     */
    private BigDecimal totalCommission = BigDecimal.ZERO;

    /**
     * 提成人佣金列表
     */
    private List<TradeCommission> commissions = new ArrayList<>();

    /**
     * 是否返利
     */
    private Boolean commissionFlag = Boolean.FALSE;

    /**
     * 正在进行的退单数量
     */
    private Integer returnOrderNum = 0;

    /**
     * 是否被结算
     */
    private Boolean hasBeanSettled;

    /**
     * 是否可退标识
     */
    private Boolean canReturnFlag;

    /**
     * 退款标识
     * 仅供结算使用 - 标识该订单是未收货的退款单子
     * <p>
     * 该单子flowState是作废不会入账，但是退单是COMPLETE状态会入账，导致收支不公，加了单独的状态作为判断
     */
    private Boolean refundFlag;

    /**
     * 订单营销信息
     */
    private List<TradeMarketingVO> tradeMarketings;

    /**
     * 订单使用的店铺优惠券
     */
    private TradeCouponVO tradeCoupon;

    /**
     * 选择的自提点信息
     */
    private TradeWareHouse tradeWareHouse;

    /**
     * 营销赠品全量列表
     */
    private List<TradeItem> gifts = new ArrayList<>();

    /**
     * 订单来源--区分h5,pc,app,小程序,代客下单
     */
    private OrderSource orderSource;

    /**
     * 订单评价状态
     */
    private EvaluateStatus orderEvaluateStatus = EvaluateStatus.NO_EVALUATE;

    /**
     * 店铺服务评价状态
     */
    private EvaluateStatus storeEvaluate = EvaluateStatus.NO_EVALUATE;

    /**
     * 支付方式
     */
    private PayWay payWay;

    /**
     * 可退积分
     */
    private Long canReturnPoints;

    /**
     * 已退金额
     */
    private BigDecimal canReturnPrice;

    /**
     *  余额支付退款金额
     */
    private BigDecimal balanceReturnPrice = BigDecimal.ZERO;

    /**
     * 订单类型 0：普通订单；1：积分订单；
     */
    private OrderType orderType;

    /**
     * 积分订单类型 0：积分商品 1：积分优惠券
     */
    private PointsOrderType pointsOrderType;

    /**
     * 分享人id
     */
    private String shareUserId;

    /**
     * 分享人id
     */
    private String wareHouseCode;

    /**
     * 仓库名称，老邓头需求
     */
    private String wareHouseNmae;


    /**
     * 仓库编号
     */
    private Long wareId;

    /**
     * 仓库名称
     */
    private String wareName;

    /**
     * 是否是秒杀抢购商品订单
     */
    private Boolean isFlashSaleGoods;

    /**
     * 是否拼团订单
     */
    private Boolean grouponFlag = false;

    /**
     * 订单拼团信息
     */
    private TradeGroupon tradeGroupon;

    /**
     *wms 第三方订单推送标志位
     */
    private Boolean WMSPushFlag;

    /**
     * 商品总件数
     */
    private Long goodsTotalNum;
    /**
     * 尾款订单号
     */
    private String tailOrderNo;

    /**
     * 尾款通知手机号
     */
    private String tailNoticeMobile;

    /**
     * 尾款支付单ID
     */
    private String tailPayOrderId;

    /**
     * 是否是预售商品
     */
    private Boolean isBookingSaleGoods;

    /**
     * 预售类型
     */
    private BookingType bookingType;

    /**
     * 活动类型
     */
    private String activityType;

    /**
     * 物流 第三方物流(有地址)：01 超级大白金待选物流(无地址)：02
     */
    private String logistics;

    private String logistics100;


    /**
     * 预约发货时间
     */
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate bookingDate;

    /**
     * 套装数
     */
    private Long suitBuyCount;

    /**
     * 套装id
     */
    private Long marketingId;

    /**
     * 来源渠道
     */
    private String sourceChannel;

    /**
     * 购买指定商品赠券codeId集合
     */
    @ApiModelProperty(value = "购买指定商品赠券codeId集合")
    private List<String> sendCouponCodeIds;

    /**
     * 购买指定商品赠鲸币ID集合
     */
    @ApiModelProperty(value = "购买指定商品赠鲸币ID集合")
    private List<Long> sendCoinRecordIds;

    /**
     * 订单返鲸币总数量
     */
    @ApiModelProperty(value = "订单返鲸币总数量")
    private BigDecimal returnCoin;

    /**
     * 购买指定商品赠券关联skuIds
     */
    @ApiModelProperty(value = "购买指定商品赠券关联skuIds")
    private List<String> couponSkuIds;

    /**
     * 销售类型 0批发 1销售
     */
    @ApiModelProperty(value = "销售类型 0批发 1零售")
    private SaleType saleType;

    /**
     * 乡镇件标识:默认false
     */
    @ApiModelProperty(value = "乡镇件标识:默认false")
    private Boolean villageFlag = false;

    /**
     * 展示乡镇件标识:默认false
     */
    @ApiModelProperty(value = "展示乡镇件标识:默认false")
    private Boolean newVilageFlag = false;

    /**
     * 支付订单号
     */
    @ApiModelProperty(value = "支付订单")
    private String payOrderNo;

    @ApiModelProperty(value = "是否有退款流水凭证0否1是")
    private String refundVoucherImagesFlag;
    /**
     * 打印次数
     */
    @ApiModelProperty(value = "打印次数统计")
    private Integer printCount;

    /**
     * 防止校验时发生 Null pointer exception
     * 只有提货订单才会有订单类型
     * @return
     */
    public String getActivityType() {
        if (Objects.isNull(activityType)){
            activityType = TradeActivityTypeEnum.TRADE.toActivityType();
        }
        return activityType;
    }

    /**
     * 增加
     *
     * @param log
     * @return
     */
    public List<TradeEventLog> appendTradeEventLog(TradeEventLog log) {
        tradeEventLogs.add(0, log);
        return tradeEventLogs;
    }

    /**
     * 增加发货单
     *
     * @param tradeDeliver 收款单信息
     */
    public void addTradeDeliver(TradeDeliver tradeDeliver) {
        tradeDelivers.add(0, tradeDeliver);
    }


    /**
     * @return
     */
    @JsonIgnore
    public ConcurrentHashMap<String, List<TradeItem>> skuItemMap() {


        return new ConcurrentHashMap<>(
                tradeItems.stream().collect(Collectors.groupingBy(TradeItem::getSkuId)));

//        return new ConcurrentHashMap<>(
//                tradeItems.stream().collect(Collectors.toMap(TradeItem::getSkuId, Function.identity())));
    }

    /**
     * @return
     */
    @JsonIgnore
    public ConcurrentHashMap<Long, List<TradeItem>> skuDevanningItemMap() {


        return new ConcurrentHashMap<>(
                tradeItems.stream().collect(Collectors.groupingBy(TradeItem::getDevanningId)));

//        return new ConcurrentHashMap<>(
//                tradeItems.stream().collect(Collectors.toMap(TradeItem::getSkuId, Function.identity())));
    }

    /**
     * @return
     */
    @JsonIgnore
    public ConcurrentHashMap<String, List<TradeItem>> giftSkuItemMap() {

        return new ConcurrentHashMap<>(
                gifts.stream().collect(Collectors.groupingBy(TradeItem::getSkuId)));

//        return new ConcurrentHashMap<>(
//                gifts.stream().collect(Collectors.toMap(TradeItem::getSkuId, Function.identity())));
    }

    @JsonIgnore
    public ConcurrentHashMap<String, TradeItem> giftSkuItemMap2() {
        return new ConcurrentHashMap<>(
                gifts.stream().collect(Collectors.toMap(TradeItem::getSkuId, Function.identity())));
    }

    @JsonIgnore
    public ConcurrentHashMap<Long, TradeItem> devanningSkuItemMap() {
        return new ConcurrentHashMap<>(
                tradeItems.stream().collect(Collectors.toMap(TradeItem::getDevanningId, Function.identity())));
    }

    @JsonIgnore
    public ConcurrentHashMap<String, TradeItem> skuItemMap2() {
        return new ConcurrentHashMap<>(
                tradeItems.stream().collect(Collectors.toMap(TradeItem::getSkuId, Function.identity())));
    }

}
