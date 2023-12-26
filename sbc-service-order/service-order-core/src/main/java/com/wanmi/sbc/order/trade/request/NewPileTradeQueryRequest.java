package com.wanmi.sbc.order.trade.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.ChannelType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.OrderType;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.marketing.bean.vo.TradeGrouponVO;
import com.wanmi.sbc.order.bean.enums.*;
import com.wanmi.sbc.order.trade.model.entity.NewPileTradeState;
import com.wanmi.sbc.order.util.XssUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>订单查询参数结构</p>
 * Created by of628-wenzhi on 2017-07-18-下午3:25.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewPileTradeQueryRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 149142593703964072L;

    /**
     * 主订单编号
     */
    private String id;

    /**
     * 子订单编号
     */
    private String sonOrderIdAccount;

    /**
     * 父订单号，用于组织批量订单合并支付，目前仅在支付与退款中使用。
     */
    private String parentId;

    /**
     * 客户名称-模糊查询
     */
    private String buyerName;

    /**
     * 客户名称
     */
    private String buyerId;

    /**
     * 客户账号-模糊查询
     */
    private String buyerAccount;

    /**
     * 商品名称-模糊查询
     */
    private String skuName;
    private String skuNo;

    /**
     * 供应商-模糊查询
     */
    private String providerName;

    private String providerCode;

    /**
     * 收货人-模糊查询
     */
    private String consigneeName;
    private String consigneePhone;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private Long provinceId;

    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    private Long cityId;

    /**
     * 区
     */
    @ApiModelProperty(value = "区")
    private Long areaId;

    /**
     * 订单状态-精确查询
     */
    private NewPileTradeState tradeState;

    /**
     * 用于根据ids批量查询场景
     */
    private String[] ids;

    /**
     * 退单创建开始时间，精确到天
     */
    private String beginTime;

    /**
     * 退单创建结束时间，精确到天
     */
    private String endTime;

    /**
     * 客户端条件搜索时使用，订单编号或商品名称
     */
    private String keyworks;

    /**
     * 商家id-精确查询
     */
    private Long supplierId;

    /**
     * 商家编码-模糊查询
     */
    private String supplierCode;

    /**
     * 商家名称-模糊查询
     */
    private String supplierName;

    /**
     * 店铺Id
     */
    private Long storeId;
    /**
     * 已完成订单允许申请退单时间
     */
    private Integer day;

    /**
     * 是否允许退单
     */
    private Integer status;

    /**
     * 业务员id
     */
    private String employeeId;

    /**
     * 业务员id集合
     */
    private List<String> employeeIds;

    /**
     * 客户id
     */
    private Object[] customerIds;

    /**
     * 是否boss或商家端
     */
//    @Builder.Default
    private Boolean isBoss;

    /**
     * 批量流程状态
     */
    private List<NewPileFlowState> flowStates;

    /**
     * 批量非流程状态
     */
    private List<NewPileFlowState> notFlowStates;

    /**
     * 订单支付顺序
     */
    private PaymentOrder paymentOrder;

    /**
     * 开始支付时间
     */
    private LocalDateTime startPayTime;

    /**
     * 邀请人id
     */
    private String inviteeId;

    /**
     * 分销渠道类型
     */
    private ChannelType channelType;


    /**
     * 小b端我的客户列表是否是查询全部
     */
    @ApiModelProperty(value = "小b端我的客户列表是否是查询全部")
    private boolean customerOrderListAllType;


    /**
     * 是否拼团订单
     */
    @ApiModelProperty(value = "是否拼团订单")
    private Boolean grouponFlag;

    /**
     * 订单拼团信息
     */
    private TradeGrouponVO tradeGroupon;

    /**
     * 订单类型 0：普通订单；1：积分订单
     */
    @ApiModelProperty(value = "订单类型")
    private OrderType orderType;

    /**
     * 订单完成开始时间
     */
    private String completionBeginTime;

    /**
     * 订单完成结束时间
     */
    private String completionEndTime;

    /**
     * 支付单ID
     */
    private String payOrderId;

    /**
     * 支付单编号
     */
    private String payOrderNo;

    /**
     * 配送方式
     */
    private DeliverWay deliverWay;

    /**
     * 尾款订单号
     */
    private String tailOrderNo;

    /**
     * 活动类型
     */
    private String activityType;

    /**
     * 仓库id
     */
    private Long wareId;

    /**
     * 是否使用鲸贴，0：否，1：是
     */
    private Integer useBalancePrice;


    /**
     * @return
     */
    public Boolean getIsBoss() {
        if (this.isBoss == null) {
            return true;
        }
        return this.isBoss;
    }

    /**
     * 封装公共条件
     *
     * @return
     */
    private List<Criteria> getCommonCriteria() {
        List<Criteria> criterias = new ArrayList<>();

        //判断--小b端我的客户列表是否是查询全部
        if (customerOrderListAllType) {
            criterias.add(Criteria.where("tradeState.payState").in(PayState.PAID));
            criterias.add(Criteria.where("tradeState.flowState").in(FlowState.AUDIT
                    , NewPileFlowState.PICK_PART
                    , NewPileFlowState.PILE
                    , NewPileFlowState.COMPLETED
                    , NewPileFlowState.VOID));
        }

        // 订单编号
        if (StringUtils.isNoneBlank(id)) {
            criterias.add(XssUtils.regex("id", id));
        }

        // 父订单号
        if(StringUtils.isNotEmpty(parentId)){
            criterias.add(Criteria.where("parentId").is(parentId));
        }

        if(useBalancePrice != null){
            if(useBalancePrice == 0){// 未使用鲸贴
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("tradePrice.balancePrice").exists(false),Criteria.where("tradePrice.balancePrice").lte("0.00"));
                criterias.add(criteria);
            }else if(useBalancePrice == 1){// 使用鲸贴
                criterias.add(Criteria.where("tradePrice.balancePrice").exists(true).gt("0.00"));
            }
        }

        //批量订单编号
        if (Objects.nonNull(ids) && ids.length > 0) {
            criterias.add(Criteria.where("id").in(Arrays.asList(ids)));
        }

        //时间范围，大于开始时间
        if (StringUtils.isNotBlank(beginTime)) {
            criterias.add(Criteria.where("tradeState.createTime").gte(DateUtil.parseDayTime(beginTime)));
        }

        //小与传入的结束时间+1天，零点前
        if (StringUtils.isNotBlank(endTime)) {
            criterias.add(Criteria.where("tradeState.createTime").lt(DateUtil.parseDayTime(endTime)));
        }

        //订单完成日期，大于/等于开始时间
        if (StringUtils.isNotBlank(completionBeginTime)) {
            criterias.add(Criteria.where("tradeState.endTime").gte(DateUtil.parseDay(completionBeginTime)));
        }
        //小与传入的结束时间+1天，零点前
        if (StringUtils.isNotBlank(completionEndTime)) {
            criterias.add(Criteria.where("tradeState.endTime").lt(DateUtil.parseDay(completionEndTime).plusDays(1)));
        }

        //所属业务员
        if (StringUtils.isNotBlank(employeeId)) {
            criterias.add(Criteria.where("buyer.employeeId").is(employeeId));
        }

        if (CollectionUtils.isNotEmpty(employeeIds)) {
            criterias.add(Criteria.where("buyer.employeeId").in(employeeIds));
        }

        //商家ID
        if (Objects.nonNull(supplierId)) {
            criterias.add(Criteria.where("supplier.supplierId").is(supplierId));
        }

        if (Objects.nonNull(storeId)) {
            criterias.add(Criteria.where("supplier.storeId").is(storeId));
        }

        //批量客户
        if (StringUtils.isNotBlank(buyerId)) {
            criterias.add(Criteria.where("buyer.id").is(buyerId));
        }

        //批量客户
        if (Objects.nonNull(customerIds) && customerIds.length > 0) {
            criterias.add(Criteria.where("buyer.id").in(customerIds));
        }

        // 发货状态
        if (Objects.nonNull(tradeState)) {
            // 发货状态
            if (Objects.nonNull(tradeState.getDeliverStatus())) {
                criterias.add(Criteria.where("tradeState.deliverStatus").is(tradeState.getDeliverStatus().getStatusId()));
            }

            // 支付状态
            if (Objects.nonNull(tradeState.getPayState())) {
                criterias.add(Criteria.where("tradeState.payState").is(tradeState.getPayState().getStateId()));
                if (tradeState.getPayState() == PayState.NOT_PAID) {
                    criterias.add(Criteria.where("tradeState.flowState").ne(FlowState.VOID.getStateId()));
                }
            }

            // 流程状态
            if (Objects.nonNull(tradeState.getFlowState())) {
                criterias.add(Criteria.where("tradeState.flowState").is(tradeState.getFlowState().getStateId()));
            }

            //订单来源
            if (Objects.nonNull(tradeState.getOrderSource())) {
                criterias.add(Criteria.where("orderSource").is(tradeState.getOrderSource().toValue()));
            }
        }

        //支付单id
        if (Objects.nonNull(payOrderId)) {
            criterias.add(Criteria.where("payOrderId").regex(payOrderNo));
        }
        // 支付单编号
        if(Objects.nonNull(payOrderNo)){
            String condition = payOrderNo.startsWith("31YT") ? payOrderNo.substring("31YT".length()) : payOrderNo;
            criterias.add(Criteria.where("payOrderNo").regex(condition));
        }

        //批量流程状态
        if (CollectionUtils.isNotEmpty(flowStates)) {
            criterias.add(Criteria.where("tradeState.flowState").in(flowStates.stream().map(NewPileFlowState::getStateId).collect(Collectors.toList())));
        }

        if (StringUtils.isNotBlank(supplierCode)) {
            criterias.add(XssUtils.regex("supplier.supplierCode", supplierCode));
        }

        if (StringUtils.isNotBlank(supplierName)) {
            criterias.add(XssUtils.regex("supplier.supplierName", supplierName));
        }

        //供应商名称 模糊查询
        if (StringUtils.isNoneBlank(providerName)) {
            criterias.add(XssUtils.regex("tradeItems.providerName", providerName));
        }
        //供应商编号
        if (StringUtils.isNoneBlank(providerCode)) {
            criterias.add(XssUtils.regex("tradeItems.providerCode", providerCode));
        }

        // 客户名称-模糊查询
        if (StringUtils.isNotBlank(buyerName)) {
            criterias.add(XssUtils.regex("buyer.name", buyerName));
        }

        // 客户账号-模糊查询
        if (StringUtils.isNotBlank(buyerAccount)) {
            criterias.add(XssUtils.regex("buyer.account", buyerAccount));
        }

        // 收货人
        if (StringUtils.isNotBlank(consigneeName)) {
            criterias.add(XssUtils.regex("consignee.name", consigneeName));
        }

        // 收货电话
        if (StringUtils.isNotBlank(consigneePhone)) {
            criterias.add(XssUtils.regex("consignee.phone", consigneePhone));
        }

        // skuName模糊查询
        if (StringUtils.isNotBlank(skuName)) {
            Criteria orCriteria = new Criteria();
            orCriteria.orOperator(
                    XssUtils.regex("tradeItems.skuName", skuName),
                    XssUtils.regex("gifts.skuName", skuName));
            criterias.add(orCriteria);
        }

        // skuNo模糊查询
        if (StringUtils.isNotBlank(skuNo)) {
            Criteria orCriteria = new Criteria();
            orCriteria.orOperator(
                    XssUtils.regex("tradeItems.skuNo", skuNo),
                    XssUtils.regex("gifts.skuNo", skuNo));
            criterias.add(orCriteria);
        }

        //关键字
        if (StringUtils.isNotBlank(keyworks)) {
            Criteria orCriteria = new Criteria();
            orCriteria.orOperator(
                    XssUtils.regex("id", keyworks),
                    XssUtils.regex("tradeItems.skuName", keyworks),
                    XssUtils.regex("gifts.skuName", keyworks));
            criterias.add(orCriteria);
        }

        //批量流程状态
        if (CollectionUtils.isNotEmpty(notFlowStates)) {
            criterias.add(Criteria.where("tradeState.flowState").nin(notFlowStates.stream().map(NewPileFlowState::getStateId).collect(Collectors.toList())));
        }

        // 订单支付顺序
        if (Objects.nonNull(paymentOrder)) {
            criterias.add(Criteria.where("paymentOrder").is(paymentOrder.getStateId()));
        }

        // 订单开始支付时间，开始支付的订单，进行锁定，不能进行其他操作，比如未支付超时作废
        if (Objects.nonNull(startPayTime)) {
            criterias.add(new Criteria().orOperator(Criteria.where("tradeState.startPayTime").lt(startPayTime), Criteria.where("tradeState.startPayTime").exists(false)));
        }

        //分销渠道类型和邀请人ID不为空
        if (Objects.nonNull(channelType) && Objects.nonNull(inviteeId) && !"".equals(inviteeId)) {
            Criteria andCriteria = new Criteria();
            andCriteria.andOperator(Criteria.where("channelType").is(channelType.toString()), Criteria.where("distributionShareCustomerId").is(inviteeId));
//            criterias.add(new Criteria().orOperator(andCriteria,Criteria.where("storeBagsFlag").is(DefaultFlag.YES)));
            criterias.add(new Criteria().orOperator(andCriteria, Criteria.where("storeBagsInviteeId").is(inviteeId)));
        } else {
            // 邀请人id
            if (Objects.nonNull(inviteeId) && !"".equals(inviteeId)) {
                criterias.add(Criteria.where("inviteeId").is(inviteeId));
            }
        }

        //订单类型
        if (Objects.nonNull(orderType)) {
            if (orderType != OrderType.ALL_ORDER) {
                criterias.add(Criteria.where("orderType").is(orderType));
            }
        }

        // 是否拼团订单
        if (Objects.nonNull(grouponFlag)) {
            criterias.add(Criteria.where("grouponFlag").is(grouponFlag));
        }

        if (Objects.nonNull(tradeGroupon)) {
            // 是否团长订单
            if (Objects.nonNull(tradeGroupon.getLeader())) {
                criterias.add(Criteria.where("tradeGroupon.leader").is(tradeGroupon.getLeader()));
            }

            // 团订单状态
            if (Objects.nonNull(tradeGroupon.getGrouponOrderStatus())) {
                criterias.add(Criteria.where("tradeGroupon.grouponOrderStatus").is(tradeGroupon.getGrouponOrderStatus
                        ().toString()));
            }

            // 团编号
            if (Objects.nonNull(tradeGroupon.getGrouponNo())) {
                criterias.add(Criteria.where("tradeGroupon.grouponNo").is(tradeGroupon.getGrouponNo()));
            }

            // 团活动id
            if (Objects.nonNull(tradeGroupon.getGrouponActivityId())) {
                criterias.add(Criteria.where("tradeGroupon.grouponActivityId").is(tradeGroupon.getGrouponActivityId()));
            }

            // 团商品
            if (Objects.nonNull(tradeGroupon.getGoodInfoId())) {
                criterias.add(Criteria.where("tradeGroupon.goodInfoId").is(tradeGroupon.getGoodInfoId()));
            }
        }

        // 查询配送方式
        if (Objects.nonNull(deliverWay)) {
            criterias.add(Criteria.where("deliverWay").is(deliverWay));
        }

        //省
        if (Objects.nonNull(provinceId)) {
            criterias.add(Criteria.where("consignee.provinceId").is(provinceId));
        }
        //市
        if (Objects.nonNull(cityId)) {
            criterias.add(Criteria.where("consignee.cityId").is(cityId));
        }
        //区
        if (Objects.nonNull(areaId)) {
            criterias.add(Criteria.where("consignee.areaId").is(areaId));
        }
        /**
         * 活动类型
         */
        if (Objects.nonNull(activityType)) {
            criterias.add(Criteria.where("activityType").is(activityType));
        }
        /**
         * 仓库id
         */
        if (Objects.nonNull(wareId) && wareId > 0) {
            criterias.add(Criteria.where("wareId").is(wareId));
        }

        return criterias;
    }

    /**
     * 公共条件
     *
     * @return
     */
    public Criteria getWhereCriteria() {
        List<Criteria> criteriaList = this.getCommonCriteria();
        if (CollectionUtils.isEmpty(criteriaList)) {
            return new Criteria();
        }
        return new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
    }

    /**
     * 设定状态条件逻辑
     * 已审核状态需筛选出已审核与部分发货
     */
    public void makeAllAuditFlow() {
//        if(Objects.nonNull(tradeState) && tradeState.getFlowState() ==  FlowState.AUDIT){
//        if (Objects.nonNull(tradeState)) {
//            //待发货状态需筛选出未发货与部分发货
//            tradeState.setFlowState(null);
//            flowStates = Arrays.asList(NewPileFlowState.AUDIT, NewPileFlowState.DELIVERED_PART);
//        }
    }

    /**
     * 可退订单的条件
     *
     * @return
     */
    public Criteria getCanReturnCriteria() {
        /**
         * 允许退单
         */
        if (Objects.nonNull(status) && status == 0) {
            if (tradeState == null) {
                tradeState = new NewPileTradeState();
            }
            tradeState.setDeliverStatus(DeliverStatus.NOT_YET_SHIPPED);
        }

        List<Criteria> criteria = this.getCommonCriteria();

        Criteria dayCriteria = new Criteria();
        // 开店礼包不支持退单
        criteria.add(Criteria.where("storeBagsFlag").ne(DefaultFlag.YES));

        //已完成订单允许申请退单时间
        if (Objects.nonNull(day) && day > 0) {
            dayCriteria.andOperator(
                    Criteria.where("tradeState.flowState").is(NewPileFlowState.COMPLETED.getStateId()),
                    Criteria.where("tradeState.endTime").gte(LocalDateTime.now().minusDays(day))
            );
        } else {
            dayCriteria.andOperator(Criteria.where("tradeState.flowState").is(NewPileFlowState.COMPLETED.getStateId()));
        }

//        criteria.add(
//                new Criteria().orOperator(
//                        dayCriteria,
//                        new Criteria().andOperator(
//                                Criteria.where("tradeState.deliverStatus").is(DeliverStatus.NOT_YET_SHIPPED.getStatusId()),
//                                Criteria.where("tradeState.payState").is(PayState.PAID.getStateId()),
//                                Criteria.where("tradeState.auditState").is(AuditState.CHECKED.getStatusId()),
//                                Criteria.where("tradeState.flowState").nin(
//                                        NewPileFlowState.VOID.getStateId(), NewPileFlowState.INIT.getStateId())
//                        )
//                )
//        );
        return new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()]));
    }


    @Override
    public String getSortColumn() {
        return "tradeState.createTime";
    }

    @Override
    public String getSortRole() {
        return "desc";
    }

    @Override
    public String getSortType() {
        return "Date";
    }
}
