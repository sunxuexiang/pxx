package com.wanmi.sbc.order.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.account.bean.enums.RefundStatus;
import com.wanmi.sbc.common.enums.ChannelType;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.customer.bean.vo.CustomerAccountVO;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.order.bean.enums.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class NewPileReturnOrderVO implements Serializable {

    private static final long serialVersionUID = 430323163025418853L;
    /**
     * 退单号
     */
    @ApiModelProperty(value = "退单号")
    private String id;

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
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
     * 区分大白鲸与连锁
     */
    @ApiModelProperty(value = "区分大白鲸与连锁")
    private String sourceChannel;

    /**
     * 子订单
     */
    private TradeVO tradeVO;

    /**
     * 客户信息 买家信息
     */
    @ApiModelProperty(value = "客户信息 买家信息")
    private BuyerVO buyer;

    /**
     * 客户账户信息
     */
    @ApiModelProperty(value = "客户账户信息")
    private CustomerAccountVO customerAccount;

    /**
     * 商家信息
     */
    @ApiModelProperty(value = "商家信息")
    private CompanyVO company;

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
     * 退货方式
     */
    @ApiModelProperty(value = "退货方式")
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
    private List<ReturnItemVO> returnItems;

    /**
     * 退单赠品信息
     */
    @ApiModelProperty(value = "退单赠品信息")
    private List<ReturnItemVO> returnGifts = new ArrayList<>();

    /**
     * 退货总金额
     */
    @ApiModelProperty(value = "退货总金额")
    private ReturnPriceVO returnPrice;

    /**
     * 退积分信息
     */
    @ApiModelProperty(value = "退积分信息")
    private ReturnPointsVO returnPoints;

    /**
     * 收货人信息
     */
    @ApiModelProperty(value = "收货人信息")
    private ConsigneeVO consignee;

    /**
     * 退货物流信息
     */
    @ApiModelProperty(value = "退货物流信息")
    private ReturnLogisticsVO returnLogistics;

    /**
     * 退货单状态
     */
    @ApiModelProperty(value = "退货单状态")
    private NewPileReturnFlowState returnFlowState;

    /**
     * 退货日志记录
     */
    @ApiModelProperty(value = "退货日志记录")
    private List<ReturnEventLogVO> returnEventLogs = new ArrayList<>();

    /**
     * 拒绝原因
     */
    @ApiModelProperty(value = "拒绝原因")
    private String rejectReason;

    /**
     * 支付方式枚举
     */
    @ApiModelProperty(value = "支付方式枚举")
    private PayType payType;

    /**
     * 支付渠道枚举
     */
    @ApiModelProperty(value = "支付渠道枚举")
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
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 退单完成时间
     */
    @ApiModelProperty(value = "退单完成时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime finishTime;

    /**
     * 是否被结算
     */
    @ApiModelProperty(value = "是否被结算", dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private Boolean hasBeanSettled;

    /**
     * 邀请人id
     */
    @ApiModelProperty(value = "邀请人id，用于查询从店铺精选下的单")
    private String inviteeId;

    /**
     * 分销渠道类型
     */
    @ApiModelProperty(value = "分销渠道类型")
    private ChannelType channelType;

    /**
     * 退款失败原因
     */
    private String refundFailedReason;


    /**
     * 基础分销设置-小店名称
     */
    @ApiModelProperty(value = "小店名称")
    private String shopName;

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
     * 业务员名称
     */
    @ApiModelProperty(value = "业务员名称")
    private String employeeName;
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
     * 活动类型 0:销售订单  1:提货 2:为囤货 3:新提货订单 4:新囤货订单
     */
    private String activityType;

    //TODO RD 联调定义
    @ApiModelProperty(value = "鲸贴返回类型：外省用户返还，本省用户返还")
    private Integer returnTypeOfJingTie;

    @ApiModelProperty(value = "鲸贴返回金额")
    private BigDecimal returnAmountOfJingTie;

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

    @ApiModelProperty(value = "是否进入财务退款 false 否，true 是")
    private Boolean financialRefundFlag = false;

    /**
     * 增加操作日志
     *
     * @param log
     * @return
     */
    public List<ReturnEventLogVO> appendReturnEventLog(ReturnEventLogVO log) {
        if (returnEventLogs == null) {
            returnEventLogs = new ArrayList<>();
        }
        returnEventLogs.add(0, log);
        return returnEventLogs;
    }

    /**
     * 对比
     *
     * @param returnOrder
     * @return
     */
    public DiffResult diff(NewPileReturnOrderVO returnOrder) {
        return new DiffBuilder(this, returnOrder, ToStringStyle.JSON_STYLE)
                .append("returnReason", returnReason, returnOrder.getReturnReason())
                .append("returnWay", returnWay, returnOrder.getReturnWay())
                .append("returnPrice", returnPrice, returnOrder.getReturnPrice())
                .append("returnPoints", returnPoints, returnOrder.getReturnPoints())
                .append("returnItems", returnItems, returnOrder.getReturnItems())
                .append("description", description, returnOrder.getDescription())
                .append("images", images, returnOrder.getImages())
                .build();
    }

    public void merge(NewPileReturnOrderVO newReturnOrder) {
        DiffResult diffResult = this.diff(newReturnOrder);
        diffResult.getDiffs().stream().forEach(
                diff -> {
                    String fieldName = diff.getFieldName();
                    switch (fieldName) {
                        case "returnReason":
                            mergeSimple(fieldName, diff.getRight());
                            break;
                        case "returnWay":
                            mergeSimple(fieldName, diff.getRight());
                            break;
                        case "description":
                            mergeSimple(fieldName, diff.getRight());
                            break;
                        case "images":
                            mergeSimple(fieldName, diff.getRight());
                            break;
                        case "returnPrice":
                            returnPrice.merge(newReturnOrder.getReturnPrice());
                            break;
                        case "returnPoints":
                            returnPoints.merge(newReturnOrder.getReturnPoints());
                            break;
                        case "returnItems":
                            if (!CollectionUtils.isEmpty(newReturnOrder.getReturnItems())) {
                                Map<String, ReturnItemVO> returnItemMap = newReturnOrder.getReturnItems().stream().collect(Collectors.toMap(
                                        ReturnItemVO::getSkuId, Function.identity()
                                ));
                                returnItems.stream().forEach(
                                        returnItem -> returnItem.merge(returnItemMap.get(returnItem.getSkuId()))
                                );
                            }
                            break;
                        default:
                            break;
                    }
                }
        );
    }


    /**
     * 拼接所有diff
     *
     * @param returnOrder
     * @return
     */
    public List<String> buildDiffStr(NewPileReturnOrderVO returnOrder) {
        DiffResult diffResult = this.diff(returnOrder);
        return buildDiffStr(diffResult, returnOrder).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<String> buildDiffStr(DiffResult diffResult, NewPileReturnOrderVO returnOrder) {
        Function<Object, String> f = (s) -> {
            if (s == null || StringUtils.isBlank(s.toString())) {
                return "空";
            } else {
                return s.toString().trim();
            }
        };
        return diffResult.getDiffs().stream().map(
                diff -> {
                    if (!(
                            (diff.getLeft() == null || StringUtils.isBlank(diff.getLeft().toString()))
                                    &&
                                    (diff.getRight() == null || StringUtils.isBlank(diff.getRight().toString()))
                    )) {
                        String result = null;
                        switch (diff.getFieldName()) {
                            case "returnReason":
                                result = String.format("退货原因由 %s 变更为 %s",
                                        f.apply(((ReturnReason) diff.getLeft()).getDesc()),
                                        f.apply(((ReturnReason) diff.getRight()).getDesc())
                                );
                                break;
                            case "description":
                                result = String.format("退货说明由 %s 变更为 %s",
                                        f.apply(diff.getLeft()),
                                        f.apply(diff.getRight())
                                );
                                break;
                            case "returnWay":
                                result = String.format("退货方式由 %s 变更为 %s",
                                        f.apply(((ReturnWay) diff.getLeft()).getDesc()),
                                        f.apply(((ReturnWay) diff.getRight()).getDesc())
                                );
                                break;

                            case "returnPrice":
                                result = StringUtils.join(returnPrice.buildDiffStr(returnOrder.getReturnPrice())
                                                .stream().filter(StringUtils::isNotBlank).collect(Collectors.toList())
                                        , ",");
                                break;

                            case "returnPoints":
                                result = StringUtils.join(returnPoints.buildDiffStr(returnOrder.getReturnPoints())
                                                .stream().filter(StringUtils::isNotBlank).collect(Collectors.toList())
                                        , ",");
                                break;

                            case "returnItems":
                                if (!CollectionUtils.isEmpty(returnItems) && !CollectionUtils.isEmpty(returnOrder.getReturnItems())) {
                                    Map<String, ReturnItemVO> oldMap = returnItems.stream().collect(Collectors.toMap(ReturnItemVO::getSkuId, Function.identity()));
                                    List<String> results = returnOrder.getReturnItems().stream().map(
                                            t -> StringUtils.join(oldMap.get(t.getSkuId()).buildDiffStr(t), ",")
                                    ).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                                    if (!results.isEmpty()) {
                                        result = StringUtils.join(results, ",");
                                    }
                                }
                                break;
                            case "images":
                                result = String.format("修改退单附件");
                                break;
                            default:
                                break;
                        }
                        return result;
                    }
                    return null;
                }
        ).collect(Collectors.toList());
    }

    private void mergeSimple(String fieldName, Object right) {
        Field field = ReflectionUtils.findField(NewPileReturnOrderVO.class, fieldName);
        try {
            field.setAccessible(true);
            field.set(this, right);
        } catch (IllegalAccessException e) {
            throw new SbcRuntimeException("K-050113", new Object[]{NewPileReturnOrderVO.class, fieldName});
        }
    }

    /**
     * 把状态描述 "已XX" 改为 "待XX"
     *
     * @param state ReturnFlowState
     * @return desc
     */
    public String transformReturnFlowState(ReturnFlowState state) {

        if (Objects.equals(this.returnType, ReturnType.RETURN)) {

            // 退货
            switch (state) {
                case INIT:
                    return "待审核";
                case AUDIT:
                    return "待填写物流信息";
                case DELIVERED:
                    return "待商家收货";
                case RECEIVED:
                    return "待退款";
                case COMPLETED:
                    return "已完成";
                case REJECT_RECEIVE:
                    return "拒绝收货";
                case REJECT_REFUND:
                    return "拒绝退款";
                case VOID:
                    return "已作废";
                default:
                    return "";
            }

        } else {

            // 仅退款
            switch (state) {
                case INIT:
                    return "待审核";
                case AUDIT:
                    return "待退款";
                case COMPLETED:
                    return "已完成";
                case REJECT_REFUND:
                    return "拒绝退款";
                case VOID:
                    return "已作废";
                default:
                    return "";
            }

        }
    }

}

