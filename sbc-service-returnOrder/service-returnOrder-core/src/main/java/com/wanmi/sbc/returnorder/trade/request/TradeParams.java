package com.wanmi.sbc.returnorder.trade.request;


import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.common.base.DistributeChannel;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.customer.bean.vo.CommonLevelVO;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.NetWorkVO;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.returnorder.bean.enums.OrderSource;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeGrouponCommitForm;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeItem;
import com.wanmi.sbc.returnorder.trade.model.entity.value.*;
import com.wanmi.sbc.returnorder.trade.model.root.TradeItemGroup;
import com.wanmi.sbc.returnorder.trade.model.root.TradeWareHouse;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 提交订单公用方法的参数类型
 * (定义成一个类,是为了后面方便扩展字段)
 * @author bail
 * @date 2018/5/5.13:22
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeParams {
    /**
     * 是否后端操作(true:后端代客下单/修改订单 false:前端客户下单)
     */
    private boolean backendFlag;
    /**
     * 是否为下单(true:下单, false:修改订单)
     */
    private boolean commitFlag;
    /**
     * 营销活动
     */
    private List<TradeMarketingDTO> marketingList;

    /**
     * 选择的店铺优惠券id
     */
    private String couponCodeId;

    /**
     * 订单总价
     */
    private TradePrice tradePrice;

    /**
     * 订单商品数据
     */
    private List<TradeItem> tradeItems;
    /**
     * 旧订单商品数据，用于编辑订单的场景
     */
    private List<TradeItem> oldTradeItems;
    /**
     * 旧订单赠品数据，用于编辑订单的场景，由于旧订单赠品库存已先还回但事务未提交，因此在处理中将库存做逻辑叠加
     * 参考的LiuWZ的注释
     */
    private List<TradeItem> oldGifts;

    /**
     * 旧订单商品数据，用于编辑订单的场景
     */
    @ApiModelProperty(value = "旧散批订单商品数据，用于编辑订单的场景")
    private List<TradeItem> oldBulkTradeItems;

    /**
     * 客户等级
     */
    private CommonLevelVO storeLevel;
    /**
     * 下单客户
     */
    private CustomerVO customer;

    /**
     * 选择的自提仓库信息
     */
    private TradeWareHouse tradeWareHouse;

    /**
     * 下单用户是否分销员
     */
    private DefaultFlag isDistributor = DefaultFlag.NO;

    /**
     * 商家
     */
    private Supplier supplier;
    /**
     * 代客下单的操作人(目前不一定是业务员)
     */
    private Seller seller;
    /**
     * 订单来源方
     */
    private Platform platform;


    /**
     * 选择的收货地址id
     */
    private String consigneeId;
    /**
     * 收货地址详细信息(包含省市区)
     */
    private String detailAddress;
    /**
     * 收货地址修改时间
     */
    private String consigneeUpdateTime;
    /**
     * 填写的临时收货地址
     */
    private Consignee consignee;


    /**
     * 发票信息
     */
    private Invoice invoice;
    /**
     * 发票临时收货地址
     */
    private Consignee invoiceConsignee;


    /**
     * 配送方式，默认快递
     */
    private DeliverWay deliverWay;


    /**
     * 如果配送方式是战点自提
     */
    private NetWorkVO netWorkVO;

    /**
     * 支付类型，默认在线支付
     */
    private PayType payType;


    /**
     * 订单买家备注
     */
    private String buyerRemark;
    /**
     * 订单卖家备注
     */
    private String sellerRemark;
    /**
     * 附件, 逗号隔开
     */
    private String encloses;


    /**
     * 操作人ip
     */
    private String ip;
    /**
     * 是否强制提交，用于营销活动有效性校验，true: 无效依然提交， false: 无效做异常返回
     */
    private boolean forceCommit;

    /**
     * 订单来源--区分h5,pc,app,小程序,代客下单
     */
    private OrderSource orderSource;

    /**
     * 分销渠道
     */
    private DistributeChannel distributeChannel;

    /**
     * 是否开店礼包
     */
    private DefaultFlag storeBagsFlag;

    /**
     * 小店名称
     */
    private String shopName;

    /**
     * 平台分销设置开关
     */
    private DefaultFlag openFlag = DefaultFlag.NO;

    /**
     * 店铺分销设置开关
     */
    private DefaultFlag storeOpenFlag = DefaultFlag.NO;

    /**
     * 下单拼团相关字段
     */
    private TradeGrouponCommitForm grouponForm;

    /**
     * 分享人id
     */
    private String shareUserId;

    /**
     * 是否是秒杀抢购商品订单
     */
    private Boolean isFlashSaleGoods;

    /**
     * 仓库编号
     */
    private String wareHouseCode;

    /**
     * 仓库编号
     */
    private Long wareId;

    /**
     * 仓库名称
     */
    private String wareName;

    /**
     *  预约时间
     */
    private LocalDate bookingDate;

    /**
     *  物流公司信息
     */
    private LogisticsInfo logisticsInfo;

    /**
     *  订单快照
     */
    private TradeItemGroup group;

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
     * 销售类型 0批发 1零售
     */
    private SaleType saleType;

    private BigDecimal totalPrice;
}
