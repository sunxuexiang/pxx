package com.wanmi.sbc.returnorder.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.ChannelType;
import com.wanmi.sbc.common.enums.OrderType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.marketing.bean.vo.TradeGrouponVO;
import com.wanmi.sbc.returnorder.bean.enums.NewPileFlowState;
import com.wanmi.sbc.returnorder.bean.enums.PayState;
import com.wanmi.sbc.returnorder.bean.enums.PaymentOrder;
import com.wanmi.sbc.returnorder.bean.util.XssUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-04 13:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class NewPileTradeQueryDTO extends BaseQueryRequest {

    /**
     * 主订单编号
     */
    @ApiModelProperty(value = "主订单编号")
    private String id;

    @ApiModelProperty(value = "父订单号")
    private String parentId;

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    private List<String> tradeIds;

    /**
     * 子订单编号
     */
    @ApiModelProperty(value = "子订单编号")
    private String providerTradeId;
    /**
     * 客户名称-模糊查询
     */
    @ApiModelProperty(value = "客户名称")
    private String buyerName;

    @ApiModelProperty(value = "是否使用鲸贴，0：否，1：是")
    private Integer useBalancePrice;

    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String buyerId;

    /**
     * 客户账号-模糊查询
     */
    @ApiModelProperty(value = "客户账号 模糊查询")
    private String buyerAccount;

    /**
     * 商品名称-模糊查询
     */
    @ApiModelProperty(value = "商品名称 模糊查询")
    private String skuName;

    @ApiModelProperty(value = "商品编号 模糊查询")
    private String skuNo;

    /**
     * 收货人-模糊查询
     */
    @ApiModelProperty(value = "收货人 模糊查询")
    private String consigneeName;

    @ApiModelProperty(value = "收货人联系方式 模糊查询")
    private String consigneePhone;

    /**
     * 供应商-模糊查询
     */
    @ApiModelProperty(value = "供应商编号 模糊查询")
    private String providerCode;

    /**
     * 订单状态-精确查询
     */
    @ApiModelProperty(value = "订单状态 精确查询")
    private NewPileTradeStateDTO tradeState;

    /**
     * 用于根据ids批量查询场景
     */
    @ApiModelProperty(value = "用于根据ids批量查询场景")
    private Object[] ids;

    /**
     * 退单创建开始时间，精确到天
     */
    @ApiModelProperty(value = "退单创建开始时间,精确到天")
    private String beginTime;

    /**
     * 退单创建结束时间，精确到天
     */
    @ApiModelProperty(value = "退单创建结束时间,精确到天")
    private String endTime;

    /**
     * 客户端条件搜索时使用，订单编号或商品名称
     */
    @ApiModelProperty(value = "客户端条件搜索时使用,订单编号或商品名称")
    private String keyworks;

    /**
     * 商家id-精确查询
     */
    @ApiModelProperty(value = "商家id 精确查询")
    private Long supplierId;

    /**
     * 商家编码-模糊查询
     */
    @ApiModelProperty(value = "商家编码 模糊查询")
    private String supplierCode;

    /**
     * 商家名称-模糊查询
     */
    @ApiModelProperty(value = "商家名称 模糊查询")
    private String supplierName;

    /**
     * 店铺Id
     */
    @ApiModelProperty(value = "店铺编码 精确查询")
    private Long storeId;

    /**
     * 已完成订单允许申请退单时间
     */
    @ApiModelProperty(value = "已完成订单允许申请退单时间")
    private Integer day;

    /**
     * 是否允许退单
     */
    @ApiModelProperty(value = "是否允许退单", dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private Integer status;

    /**
     * 业务员id
     */
    @ApiModelProperty(value = "业务员id")
    private String employeeId;

    /**
     * 业务员名称查询
     */
    @ApiModelProperty(value = "业务员姓名查询")
    private String employeeName;

    /**
     * 业务员名称查询
     */
    @ApiModelProperty(value = "业务员账号查询")
    private String employeeAccount;

    /**
     * 业务员id集合
     */
    @ApiModelProperty(value = "业务员id集合")
    private List<String> employeeIds;


    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private Object[] customerIds;

    /**
     * 是否boss或商家端
     */
    @ApiModelProperty(value = "是否boss或商家端", dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private Boolean isBoss;

    /**
     * 批量流程状态
     */
    @ApiModelProperty(value = "批量流程状态")
    private List<NewPileFlowState> flowStates;

    /**
     * 批量非流程状态
     */
    @ApiModelProperty(value = "批量非流程状态")
    private List<NewPileFlowState> notFlowStates;

    /**
     * 订单支付顺序
     */
    @ApiModelProperty(value = "订单支付顺序")
    private PaymentOrder paymentOrder;

    /**
     * 开始支付时间
     */
    @ApiModelProperty(value = "开始支付时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime startPayTime;

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
     * 小b端我的客户列表是否是查询全部
     */
    @ApiModelProperty(value = "小b端我的客户列表是否是查询全部")
    private boolean customerOrderListAllType;

    /**
     * 订单类型 0：普通订单；1：积分订单
     */
    @ApiModelProperty(value = "订单类型")
    private OrderType orderType;

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
     * 订单完成开始时间
     */
    private String completionBeginTime;

    /**
     * 订单完成结束时间
     */
    private String completionEndTime;

    /**
     * 供应商名称
     */
    @ApiModelProperty(value = "供应商名称")
    private String providerName;

    /**
     * 配送方式
     */
    @ApiModelProperty(value = "配送方式")
    private DeliverWay deliverWay;

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
     * 活动类型(3.新提货 4.新囤货)
     */
    private String activityType;

    /**
     * 仓库id
     */
    @ApiModelProperty("仓库id")
    private Long wareId;


    /**
     * 支付订单号
     */
    @ApiModelProperty("支付订单号")
    private String payOrderNo;

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
        // 订单编号
        if (StringUtils.isNoneBlank(id)) {
            criterias.add(XssUtils.regex("id", id));
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
        //多业务员查询
        if (CollectionUtils.isNotEmpty(employeeIds)) {
            criterias.add(Criteria.where("buyer.employeeId").in(employeeIds));
        }

        //商家ID
        if (Objects.nonNull(supplierId)) {
            criterias.add(Criteria.where("supplier.supplierId").is(supplierId));
        }


        //邀请人id
        if (Objects.nonNull(inviteeId)) {
            criterias.add(Criteria.where("inviteeId").is(inviteeId));
        }

        //分销渠道类型
        if (Objects.nonNull(channelType)) {
            criterias.add(Criteria.where("channelType").is(channelType.toString()));
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
            }

            // 流程状态
            if (Objects.nonNull(tradeState.getFlowState())) {
                criterias.add(Criteria.where("tradeState.flowState").is(tradeState.getFlowState().getStateId()));
            }
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
            criterias.add(new Criteria().orOperator(Criteria.where("tradeState.startPayTime").lt(startPayTime),
                    Criteria.where("tradeState.startPayTime").exists(false)));
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
                criterias.add(Criteria.where("tradeGroupon.grouponOrderStatus").is(tradeGroupon.getGrouponOrderStatus().toString()));
            }

            // 团编号
            if (Objects.nonNull(tradeGroupon.getGrouponNo())) {
                criterias.add(Criteria.where("tradeGroupon.grouponNo").is(tradeGroupon.getGrouponNo()));
            }
        }


        //订单类型
        if (Objects.nonNull(orderType)) {
            criterias.add(Criteria.where("orderType").is(orderType));
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

        //仓库id
        if (Objects.nonNull(wareId)) {
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
//        if (Objects.nonNull(tradeState) && tradeState.getFlowState() == NewPileFlowState.AUDIT) {
//        if (Objects.nonNull(tradeState)) {
//            //待发货状态需筛选出未发货与部分发货
//            tradeState.setFlowState(null);
//            flowStates = Arrays.asList(NewPileFlowState.AUDIT, NewPileFlowState.DELIVERED_PART);
//        }
    }


    /**
     * 设定待发货 及待付款状态过滤审核的订单状态
     */
    public void makeAllAuditFlowStatus() {
//        if (Objects.nonNull(tradeState) && tradeState.getFlowState() == FlowState.AUDIT) {
//        if (Objects.nonNull(tradeState)) {
//            //待发货状态需筛选出未发货与部分发货
//            tradeState.setFlowState(null);
//            tradeState.setPayState(PayState.PAID);
//            tradeState.setDeliverStatus(DeliverStatus.NOT_YET_SHIPPED);
//            flowStates = Arrays.asList(NewPileFlowState.AUDIT, NewPileFlowState.DELIVERED_PART);
//        }
        //待付款过滤待审核的订单
//        if (Objects.nonNull(tradeState) && tradeState.getPayState() == PayState.NOT_PAID) {
//            tradeState.setFlowState(NewPileFlowState.AUDIT);
//        }
    }

    /**
     * 大白鲸个人中心使用
     */
    public void makeAllAuditFlowStatusList(Integer markType) {
        if (Objects.isNull(markType) || markType.intValue() == 0) { //全部
        }else if(markType.intValue() == 1){//待支付
            tradeState.setPayState(PayState.NOT_PAID);
            flowStates = Arrays.asList(NewPileFlowState.INIT);
        }else if(markType.intValue() == 2){//待审核
            tradeState.setPayState(PayState.UNCONFIRMED);
//            flowStates = Arrays.asList(NewPileFlowState.AUDIT);
        }else if(markType.intValue() == 3){//待提货
            tradeState.setPayState(PayState.PAID);
            flowStates = Arrays.asList(NewPileFlowState.PILE);
        }else if(markType.intValue() == 4){//部分提货
            tradeState.setPayState(PayState.PAID);
            flowStates = Arrays.asList(NewPileFlowState.PICK_PART);
        }else if(markType.intValue() == 5){//已完成
            tradeState.setPayState(PayState.PAID);
            flowStates = Arrays.asList(NewPileFlowState.COMPLETED);
        }else if(markType.intValue() == 6){//已经作废
            flowStates = Arrays.asList(NewPileFlowState.VOID);
        }
    }


    /**
     * 已支付状态下-已审核、部分发货订单
     */
    public void setPayedAndAudit() {
//        if (Objects.nonNull(tradeState) && tradeState.getFlowState() == FlowState.AUDIT) {
//        if (Objects.nonNull(tradeState)) {
//            //待发货状态需筛选出未发货与部分发货
//            tradeState.setPayState(PayState.PAID);
//            tradeState.setFlowState(null);
//            flowStates = Arrays.asList(FlowState.AUDIT, FlowState.DELIVERED_PART);
//        }
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
