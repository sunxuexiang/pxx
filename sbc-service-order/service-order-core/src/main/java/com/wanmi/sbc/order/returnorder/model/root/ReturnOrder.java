package com.wanmi.sbc.order.returnorder.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.account.bean.enums.RefundStatus;
import com.wanmi.sbc.common.enums.ChannelType;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CustomLocalDateDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.customer.bean.vo.CustomerAccountVO;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.order.bean.enums.*;
import com.wanmi.sbc.order.bean.vo.ReturnOrderAddressVO;
import com.wanmi.sbc.order.bean.vo.TradeDistributeItemVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.order.returnorder.model.entity.ReturnItem;
import com.wanmi.sbc.order.returnorder.model.value.ReturnEventLog;
import com.wanmi.sbc.order.returnorder.model.value.ReturnLogistics;
import com.wanmi.sbc.order.returnorder.model.value.ReturnPoints;
import com.wanmi.sbc.order.returnorder.model.value.ReturnPrice;
import com.wanmi.sbc.order.trade.model.entity.value.Buyer;
import com.wanmi.sbc.order.trade.model.entity.value.Company;
import com.wanmi.sbc.order.trade.model.entity.value.Consignee;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 退货单
 * Created by jinwei on 19/4/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReturnOrder implements Serializable {

    private static final long serialVersionUID = -8964710078593724385L;
    /**
     * 退单号
     */
    @Id
    private String id;

    /**
     * 子订单
     */
    private TradeVO tradeVO;
    /**
     * 订单编号
     */
    @NotBlank
    private String tid;


    /**
     * 是否必须合并支付flag
     */
    private Boolean mergFlag =false;

    /**
     * 订单同级集合
     */
    private List<String> tids;


    private String ptid;

    private String providerId;

    private String providerName;

    private String providerCode;

    private Long providerCompanyInfoId;


    /**
     * 区分大白鲸与连锁
     */
    private String sourceChannel;

    /**
     * 客户信息 买家信息
     */
    private Buyer buyer;

    /**
     * 客户账户信息
     */
    private CustomerAccountVO customerAccount;

    /**
     * 商家信息
     */
    private Company company;

    /**
     * 退货原因
     */
    private ReturnReason returnReason;

    /**
     * 退货说明
     */
    private String description;

    /**
     * 支付方式
     */
    private ReturnWay returnWay;

    /**
     * 退单附件
     */
    private List<String> images;

    /**
     * 退货商品信息
     */
    private List<ReturnItem> returnItems;

    /**
     * 退单赠品信息
     */
    private List<ReturnItem> returnGifts = new ArrayList<>();

    /**
     * 退货总金额
     */
    private ReturnPrice returnPrice;

    /**
     * 退积分信息
     */
    private ReturnPoints returnPoints;

    /**
     * 收货人信息
     */
    private Consignee consignee;

    /**
     * 退货物流信息
     */
    private ReturnLogistics returnLogistics;

    /**
     * 退货单状态
     */
    private ReturnFlowState returnFlowState;

    /**
     * 退货日志记录
     */
    private List<ReturnEventLog> returnEventLogs = new ArrayList<>();

    /**
     * 分销渠道类型
     */
    private ChannelType channelType;

    /**
     * 邀请人分销员id
     */
    private String distributorId;

    /**
     * 邀请人会员id
     */
    private String inviteeId;

    /**
     * 小店名称
     */
    private String shopName;

    /**
     * 分销员名称
     */
    private String distributorName;

    /**
     * 分销单品列表
     */
    private List<TradeDistributeItemVO> distributeItems = new ArrayList<>();


    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 支付方式枚举
     */
    private PayType payType;

    /**
     * 支付渠道枚举
     */
    private PayWay payWay;

    /**
     * 退单类型
     */
    private ReturnType returnType;

    /**
     * 退单来源
     */
    private Platform platform;

    /**
     * 退款单状态
     */
    private RefundStatus refundStatus;


    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 退单完成时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime finishTime;

    /**
     * 是否被结算
     */
    private Boolean hasBeanSettled;

    /**
     * 退款失败原因
     */
    private String refundFailedReason;

    /**
     * 审核时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime auditTime;
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
     * 活动类型 0:销售订单  1:提货 2:为囤货
     */
    private String activityType;

    /**
     * 购买指定商品赠券codeId集合
     */
    private List<String> sendCouponCodeIds;

    /**
     * 购买指定商品赠券关联skuIds
     */
    @ApiModelProperty(value = "购买指定商品赠券关联skuIds")
    private List<String> couponSkuIds;

    /**
     * 销售类型 0批发 1零售
     */
    private SaleType saleType;

    /**
     * @description  退货订单退款流水凭证
     * @author  shiy
     * @date    2023/3/14 9:22
     * @params
     * @return
    */
    @ApiModelProperty(value = "是否有退款流水凭证0否1是")
    private String refundVoucherImagesFlag;

    @ApiModelProperty(value = "鲸币充值类型")
    private Integer chaimApllyType;

    @ApiModelProperty(value = "退货地址")
    private ReturnOrderAddressVO returnOrderAddressVO;


    @ApiModelProperty(value = "是否预售标识")
    private Boolean presellFlag = false;

    /**
     * 预售发货时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime presellDeliverDate;


    /**
     * 增加操作日志
     *
     * @param log
     * @return
     */
    public List<ReturnEventLog> appendReturnEventLog(ReturnEventLog log) {
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
    public DiffResult diff(ReturnOrder returnOrder) {
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

    public void merge(ReturnOrder newReturnOrder) {
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
                                //出现重复key,以最后一个值为准
                                Map<String, ReturnItem> returnItemMap = newReturnOrder.getReturnItems().stream().collect(Collectors.toMap(
                                        ReturnItem::getSkuId, Function.identity(),(first, last) -> last
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
    public List<String> buildDiffStr(ReturnOrder returnOrder) {
        DiffResult diffResult = this.diff(returnOrder);
        return buildDiffStr(diffResult, returnOrder).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<String> buildDiffStr(DiffResult diffResult, ReturnOrder returnOrder) {
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
                                    //出现重复key,以最后一个值为准
                                    Map<String, ReturnItem> oldMap = returnItems.stream().collect(Collectors.toMap(ReturnItem::getSkuId, Function.identity(), (first, last) -> last));
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
        Field field = ReflectionUtils.findField(ReturnOrder.class, fieldName);
        try {
            field.setAccessible(true);
            field.set(this, right);
        } catch (IllegalAccessException e) {
            throw new SbcRuntimeException("K-050113", new Object[]{ReturnOrder.class, fieldName});
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

    //推送wms三方退款接口
    private WMSPushState wmsPushState;

    //wms回传退货状态
    private Boolean wmsStats;

    public String getActivityType() {
        if (Objects.isNull(activityType)){
            return TradeActivityTypeEnum.TRADE.toActivityType();
        }
        return activityType;
    }
}
