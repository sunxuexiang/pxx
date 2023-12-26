package com.wanmi.sbc.order.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.account.bean.enums.RefundStatus;
import com.wanmi.sbc.common.enums.ChannelType;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.bean.enums.ReturnReason;
import com.wanmi.sbc.order.bean.enums.ReturnType;
import com.wanmi.sbc.order.bean.enums.ReturnWay;
import com.wanmi.sbc.order.bean.vo.TradeDistributeItemVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 退货单
 * Created by jinwei on 19/4/2017.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class ReturnOrderDTO implements Serializable {

    private static final long serialVersionUID = -8964710078593724385L;

    /**
     * 退单号
     */
    @ApiModelProperty(value = "退单号")
    private String id;

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号", required = true)
    @NotBlank
    private String tid;

    @ApiModelProperty(value = "子订单编号")
    private String ptid;

    @ApiModelProperty(value = "供应商ID")
    private String providerId;

    @ApiModelProperty(value = "供应商名称")
    private String providerName;

    @ApiModelProperty(value = "供应商编码")
    private String providerCode;

    @ApiModelProperty(value = "providerCompanyInfoId")
    private Long providerCompanyInfoId;

    /**
     * 客户信息 买家信息
     */
    @ApiModelProperty(value = "客户信息 买家信息")
    private BuyerDTO buyer;

    /**
     * 客户账户信息
     */
    @ApiModelProperty(value = "客户账户信息")
    private ReturnCustomerAccountDTO customerAccount;

    /**
     * 商家信息
     */
    @ApiModelProperty(value = "商家信息")
    private CompanyDTO company;

    /**
     * 退货原因
     */
    @ApiModelProperty(value = "退货原因")
    private ReturnReason returnReason;

    /**
     * 退货说明
     */
    @ApiModelProperty(value = "退货说明")
    private String description;

    /**
     * 区分大白鲸与连锁
     */
    @ApiModelProperty(value = "区分大白鲸与连锁")
    private String sourceChannel;

    /**
     * 支付方式
     */
    @ApiModelProperty(value = "支付方式")
    private ReturnWay returnWay;

    /**
     * 退单附件
     */
    @ApiModelProperty(value = "退单附件")
    private List<String> images;

    /**
     * 退货商品信息
     */
    @ApiModelProperty(value = "退货商品信息")
    private List<ReturnItemDTO> returnItems;

    /**
     * 退单赠品信息
     */
    @ApiModelProperty(value = "退单赠品信息")
    private List<ReturnItemDTO> returnGifts = new ArrayList<>();

    /**
     * 退货总金额
     */
    @ApiModelProperty(value = "退货总金额")
    private ReturnPriceDTO returnPrice;

    /**
     * 退积分信息
     */
    @ApiModelProperty(value = "退积分信息")
    private ReturnPointsDTO returnPoints;

    /**
     * 收货人信息
     */
    @ApiModelProperty(value = "收货人信息")
    private ConsigneeDTO consignee;

    /**
     * 退货物流信息
     */
    @ApiModelProperty(value = "退货物流信息")
    private ReturnLogisticsDTO returnLogistics;

    /**
     * 退货单状态
     */
    @ApiModelProperty(value = "退货单状态")
    private ReturnFlowState returnFlowState;

    /**
     * 退货日志记录
     */
    @ApiModelProperty(value = "退货日志记录")
    private List<ReturnEventLogDTO> returnEventLogs = new ArrayList<>();

    /**
     * 拒绝原因
     */
    @ApiModelProperty(value = "拒绝原因")
    private String rejectReason;

    /**
     * 支付方式枚举
     */
    @ApiModelProperty(value = "支付方式")
    private PayType payType;

    /**
     * 支付渠道枚举
     */
    @ApiModelProperty(value = "支付方式")
    private PayWay payWay;

    /**
     * 退单类型
     */
    @ApiModelProperty(value = "退单类型")
    private ReturnType returnType;

    /**
     * 退单来源
     */
    @ApiModelProperty(value = "退单来源")
    private Platform platform;

    /**
     * 退款单状态
     */
    @ApiModelProperty(value = "退款单状态")
    private RefundStatus refundStatus;

    @ApiModelProperty(value = "创建时间")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 退单完成时间
     */
    @ApiModelProperty(value = "退单完成时间")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime finishTime;

    /**
     * 是否被结算
     */
    @ApiModelProperty(value = "是否被结算", dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private Boolean hasBeanSettled;


    /**
     * 分销渠道类型
     */
    @ApiModelProperty(value = "分销渠道类型")
    private ChannelType channelType;

    /**
     * 小店名称
     */
    @ApiModelProperty(value = "小店名称")
    private String shopName;


    /**
     * 邀请人分销员id
     */
    @ApiModelProperty(value = "邀请人分销员id")
    private String distributorId;


    /**
     * 邀请人会员id
     */
    @ApiModelProperty(value = "邀请人会员id")
    private String inviteeId;

    /**
     * 分销员名称
     */
    @ApiModelProperty(value = "分销员名称")
    private String distributorName;


    /**
     * 分销单品列表
     */
    @ApiModelProperty(value = "分销单品列表")
    private List<TradeDistributeItemVO> distributeItems = new ArrayList<>();


    /**
     * 仓库编号
     */
    private Long wareId;

    /**
     * 仓库名称
     */
    private String wareName;


    /**
     * 是否需要推送wms
     */
    private Boolean pushNeeded;

    /**
     * 仓库编号
     */
    private String wareHouseCode;

    /**
     *  wms回传默认状态
     */
    private Boolean wmsStats;

    /**
     * 活动类型 0:销售订单  1:提货 2:为囤货
     */
    private String activityType;

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
     * 订单销售类型 0批发，1销售
     */
    @ApiModelProperty(value = "订单销售类型 0批发，1销售")
    private SaleType saleType;

}
