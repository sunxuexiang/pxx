package com.wanmi.sbc.returnorder.returnorder.model.root;

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
import com.wanmi.sbc.returnorder.bean.vo.TradeDistributeItemVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.returnorder.model.entity.ReturnItem;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnEventLog;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnLogistics;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnPoints;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnPrice;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Buyer;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Company;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Consignee;
import com.wanmi.sbc.returnorder.bean.enums.*;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

;

/**
 * @Description: 囤货退货单
 * @author: jiangxin
 * @create: 2021-09-28 14:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReturnPileOrder implements Serializable {

    private static final long serialVersionUID = -1746179993453245887L;

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

    private String ptid;

    private String providerId;

    private String providerName;

    private String providerCode;

    private Long providerCompanyInfoId;

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
     * 是否需要推送wms
     */
    private Boolean pushNeeded;

    /**
     * 仓库编号
     */
    private String wareHouseCode;


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
     * @param returnPileOrder
     * @return
     */
    public DiffResult diff(ReturnPileOrder returnPileOrder) {
        return new DiffBuilder(this, returnPileOrder, ToStringStyle.JSON_STYLE)
                .append("returnReason", returnReason, returnPileOrder.getReturnReason())
                .append("returnWay", returnWay, returnPileOrder.getReturnWay())
                .append("returnPrice", returnPrice, returnPileOrder.getReturnPrice())
                .append("returnPoints", returnPoints, returnPileOrder.getReturnPoints())
                .append("returnItems", returnItems, returnPileOrder.getReturnItems())
                .append("description", description, returnPileOrder.getDescription())
                .append("images", images, returnPileOrder.getImages())
                .build();
    }

    public void merge(ReturnPileOrder newReturnPileOrder) {
        DiffResult diffResult = this.diff(newReturnPileOrder);
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
                            returnPrice.merge(newReturnPileOrder.getReturnPrice());
                            break;
                        case "returnPoints":
                            returnPoints.merge(newReturnPileOrder.getReturnPoints());
                            break;
                        case "returnItems":
                            if (!CollectionUtils.isEmpty(newReturnPileOrder.getReturnItems())) {
                                Map<String, ReturnItem> returnItemMap = newReturnPileOrder.getReturnItems().stream().collect(Collectors.toMap(
                                        ReturnItem::getSkuId, Function.identity()
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
     * @param returnPileOrder
     * @return
     */
    public List<String> buildDiffStr(ReturnPileOrder returnPileOrder) {
        DiffResult diffResult = this.diff(returnPileOrder);
        return buildDiffStr(diffResult, returnPileOrder).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<String> buildDiffStr(DiffResult diffResult, ReturnPileOrder returnPileOrder) {
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
                                result = StringUtils.join(returnPrice.buildDiffStr(returnPileOrder.getReturnPrice())
                                                .stream().filter(StringUtils::isNotBlank).collect(Collectors.toList())
                                        , ",");
                                break;

                            case "returnPoints":
                                result = StringUtils.join(returnPoints.buildDiffStr(returnPileOrder.getReturnPoints())
                                                .stream().filter(StringUtils::isNotBlank).collect(Collectors.toList())
                                        , ",");
                                break;

                            case "returnItems":
                                if (!CollectionUtils.isEmpty(returnItems) && !CollectionUtils.isEmpty(returnPileOrder.getReturnItems())) {
                                    Map<String, ReturnItem> oldMap = returnItems.stream().collect(Collectors.toMap(ReturnItem::getSkuId, Function.identity()));
                                    List<String> results = returnPileOrder.getReturnItems().stream().map(
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

    /**
     * 推送wms三方退款接口
     */
    private WMSPushState wmsPushState;

    /**
     * wms回传退货状态
     */
    private Boolean wmsStats;
}
