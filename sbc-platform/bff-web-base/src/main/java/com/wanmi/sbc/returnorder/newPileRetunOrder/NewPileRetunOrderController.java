package com.wanmi.sbc.returnorder.newPileRetunOrder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.customer.api.provider.parentcustomerrela.ParentCustomerRelaQueryProvider;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaListRequest;
import com.wanmi.sbc.customer.bean.vo.ParentCustomerRelaVO;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeDeleteRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponsByCodeIdsRequest;
import com.wanmi.sbc.marketing.bean.dto.CouponCodeDTO;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileTradeProvider;
import com.wanmi.sbc.order.api.request.refund.RefundOrderNotAuditProducerRequest;
import com.wanmi.sbc.order.api.request.refund.RefundOrderResponseByReturnOrderCodeRequest;
import com.wanmi.sbc.order.api.request.returnorder.*;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.response.refund.RefundOrderResponse;
import com.wanmi.sbc.order.api.response.returnorder.NewPileReturnOrderByIdResponse;
import com.wanmi.sbc.order.bean.dto.CompanyDTO;
import com.wanmi.sbc.order.bean.dto.ReturnOrderDTO;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.bean.enums.TradeActivityTypeEnum;
import com.wanmi.sbc.order.bean.vo.*;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.request.TradeConfigGetByTypeRequest;
import com.wanmi.sbc.setting.api.response.TradeConfigGetByTypeResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Api(tags = "ReturnOrderBaseController", description = "退单基本服务API")
@RestController
@RequestMapping("/newPileRetunOrder")
@Slf4j
public class NewPileRetunOrderController {

    @Value("${new-pile-return-order.auto-refund.enabled:true}")
    boolean newPileReturnOrderAutoRefundEnabled;

    @Autowired
    private ReturnOrderProvider returnOrderProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private ParentCustomerRelaQueryProvider parentCustomerRelaQueryProvider;

    @Autowired
    private NewPileTradeProvider newPileTradeProvider;

    @Autowired
    private CouponCodeQueryProvider couponCodeQueryProvider;

    @Autowired
    private CouponCodeProvider couponCodeProvider;

    @Autowired
    private AuditQueryProvider auditQueryProvider;

    @Autowired
    private NewPileReturnOrderProvider newPileReturnOrderProvider;

    @Autowired
    private RefundOrderQueryProvider refundOrderQueryProvider;

    /**
     * 创建退款单
     *
     * @param returnOrder
     * @return
     */
    @ApiOperation(value = "囤货创建退款单")
    @RequestMapping(value = "/addRefundNewPile", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse<String> addRefundNewPile(@RequestBody @Valid ReturnOrderDTO returnOrder) {
        verifyIsReturnable(returnOrder.getTid());
        NewPileTradeVO trade =
                newPileTradeProvider.getById(TradeGetByIdRequest.builder().tid(returnOrder.getTid()).build()).getContext().getTradeVO();
        returnOrder.setCompany(CompanyDTO.builder().companyInfoId(trade.getSupplier().getSupplierId())
                .companyCode(trade.getSupplier().getSupplierCode()).supplierName(trade.getSupplier().getSupplierName())
                .storeId(trade.getSupplier().getStoreId()).storeName(trade.getSupplier().getStoreName())
                .companyType(trade.getSupplier().getCompanyType())
                .build());
        returnOrder.setChannelType(trade.getChannelType());
        returnOrder.setDistributorId(trade.getDistributorId());
        returnOrder.setInviteeId(trade.getInviteeId());
        returnOrder.setShopName(trade.getShopName());
        returnOrder.setDistributorName(trade.getDistributorName());
        returnOrder.setDistributeItems(trade.getDistributeItems());
        returnOrder.setSaleType(trade.getSaleType());
        returnOrder.setActivityType(trade.getActivityType());

        log.info("订单类型activityType:{},订单信息：{}",trade.getActivityType(),trade);
        if(Objects.nonNull(trade.getActivityType()) && TradeActivityTypeEnum.STOCKUP.toActivityType().equals(trade.getActivityType())){
            returnOrder.setActivityType(trade.getActivityType());
        }
        if (CollectionUtils.isNotEmpty(trade.getSendCouponCodeIds())) {
            returnOrder.setSendCouponCodeIds(trade.getSendCouponCodeIds());
        }

        returnOrder.setActivityType(trade.getActivityType());
        String rid = returnOrderProvider.add(ReturnOrderAddRequest.builder().returnOrder(returnOrder)
                .operator(commonUtil.getOperator()).build()).getContext().getReturnOrderId();

        if (CollectionUtils.isNotEmpty(trade.getSendCouponCodeIds())) {
            couponCodeProvider.delByCustomerIdAndCouponIds(CouponCodeDeleteRequest.builder()
                    .customerId(trade.getBuyer().getId()).couponCodeIds(trade.getSendCouponCodeIds()).build());
        }

        if(newPileReturnOrderAutoRefundEnabled && Objects.nonNull(trade.getPayInfo()) && PayType.ONLINE.toValue() == Integer.parseInt(trade.getPayInfo().getPayTypeId())
                && (PayWay.ALIPAY.equals(trade.getPayWay())
                || PayWay.WECHAT.equals(trade.getPayWay())
                || PayWay.CMB.equals(trade.getPayWay())
                || PayWay.BALANCE.equals(trade.getPayWay())
                || PayWay.CUPSALI.equals(trade.getPayWay())
                || PayWay.CUPSWECHAT.equals(trade.getPayWay())
                || PayWay.CCB.equals(trade.getPayWay())
        )
        ) {
            log.info("线上自动退款：订单号={}，trade.getPayInfo()={}", trade.getId(), trade.getPayInfo());
            //自动退款
            RefundOrderNotAuditProducerRequest refundOrderNotAuditProducerRequest = new RefundOrderNotAuditProducerRequest();
            refundOrderNotAuditProducerRequest.setOperator(commonUtil.getOperator());
            refundOrderNotAuditProducerRequest.setRId(rid);
            refundOrderNotAuditProducerRequest.setTId(returnOrder.getTid());
            returnOrderProvider.refundOrderNotAuditNewPileProducer(refundOrderNotAuditProducerRequest);
        } else {
            log.info("线下付款，需审核：订单号={}，trade.getPayInfo()={}，自动退款开关={}", trade.getId(), trade.getPayInfo(), newPileReturnOrderAutoRefundEnabled);
        }
        return BaseResponse.success(rid);
    }


    /**
     * 根据订单id查询退单(过滤拒绝退款、拒绝收货、已作废)
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "根据订单id查询退单(过滤拒绝退款、拒绝收货、已作废)")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单Id", required = true)
    @RequestMapping(value = "/findByTid/{tid}", method = RequestMethod.GET)
    public BaseResponse<List<ReturnOrderVO>> findByTid(@PathVariable String tid) {
        List<ReturnOrderVO> returnOrders = newPileReturnOrderProvider.newPilelistByTid(ReturnOrderListByTidRequest.builder()
                .tid(tid).build()).getContext().getReturnOrderList();
        NewPileTradeVO tradeVO =
                newPileTradeProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        List<TradeItemVO> collect = tradeVO.getTradeItems().stream().filter(param -> Objects.nonNull(param.getGoodsInfoType()) && param.getGoodsInfoType() == 1).collect(Collectors.toList());
        if (collect.size()==tradeVO.getTradeItems().size()){
            throw new SbcRuntimeException("K-050138");
        }
        return BaseResponse.success(returnOrders.stream().filter(o -> o.getReturnFlowState() != ReturnFlowState.REJECT_REFUND &&
                o.getReturnFlowState() != ReturnFlowState.REJECT_RECEIVE && o.getReturnFlowState() != ReturnFlowState.VOID).collect(Collectors.toList()));
    }

    /**
     * 查看退货订单详情和可退商品数
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "查看退货订单详情和可退商品数")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "退单Id", required = true)
    @RequestMapping(value = "/trade/{tid}", method = RequestMethod.GET)
    public BaseResponse<NewPileTradeVO> canReturnItemNumByTidNewPile(@PathVariable String tid) {
        NewPileTradeVO trade = newPileReturnOrderProvider.queryCanReturnItemNumByTidNewPile(CanReturnItemNumByTidRequest.builder()
                .tid(tid).build()).getContext();
        return BaseResponse.success(trade);
    }


    @ApiOperation(value = "囤货退款列表")
    @RequestMapping(value = "page", method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<ReturnOrderVO>> page(@RequestBody NewPileReturnOrderPageRequest request) {

        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            request.setStoreId(domainStoreRelaVO.getStoreId());
        }
        List<String> customerIds = this.getAllCustomerIds();
        customerIds.add(commonUtil.getCustomer().getCustomerId());
        request.setCustomerIds(customerIds.toArray());
        request.setInviteeId(request.getInviteeId());
        request.setChannelType(request.getChannelType());
        MicroServicePage<ReturnOrderVO> returnPage = newPileReturnOrderProvider.page(request).getContext().getReturnOrderPage();
        return BaseResponse.success(returnPage);
    }

    /**
     * 查看退单详情
     *
     * @param rid
     * @return
     */
    @ApiOperation(value = "查看退单详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/mobile/{rid}",method = RequestMethod.GET)
    public BaseResponse<NewPileReturnOrderByIdResponse> findById(@PathVariable String rid) {
        NewPileReturnOrderByIdResponse returnOrder = newPileReturnOrderProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                .build()).getContext();
        List<String> customerId = new ArrayList<>(20);
        customerId.add(commonUtil.getOperatorId());
        List<ParentCustomerRelaVO> parentCustomerRelaVOList = parentCustomerRelaQueryProvider.findAllByParentId(ParentCustomerRelaListRequest
                .builder().parentId(commonUtil.getOperatorId()).build()).getContext().getParentCustomerRelaVOList();
        if (CollectionUtils.isNotEmpty(parentCustomerRelaVOList)) {
            List<String> child = parentCustomerRelaVOList.stream().map(ParentCustomerRelaVO::getCustomerId).distinct().collect(Collectors.toList());
            customerId.addAll(child);
        }
        if (!customerId.contains(returnOrder.getBuyer().getId())) {
            throw new SbcRuntimeException("K-050100", new Object[]{rid});
        }
        return BaseResponse.success(returnOrder);
    }

    /**
     * 查询退单退款记录
     *
     * @param rid
     * @return
     */
    @ApiOperation(value = "查询退单退款记录")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/refundOrder/{rid}", method = RequestMethod.GET)
    public BaseResponse<RefundOrderResponse> refundOrder(@PathVariable String rid) {
        NewPileReturnOrderByIdResponse returnOrder = newPileReturnOrderProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build())
                .getContext();
        checkUnauthorized(returnOrder);
        RefundOrderResponse refundOrderResponse =
                newPileReturnOrderProvider.getRefundOrderReturnOrderCodeNewPile(new RefundOrderResponseByReturnOrderCodeRequest(rid)).getContext();
        return BaseResponse.success(refundOrderResponse);

    }

    private void checkUnauthorized(NewPileReturnOrderByIdResponse returnOrder) {
        List<String> customerId = new ArrayList<>();
        customerId.add(commonUtil.getOperatorId());
        List<ParentCustomerRelaVO> parentCustomerRelaVOList = parentCustomerRelaQueryProvider.findAllByParentId(ParentCustomerRelaListRequest
                .builder().parentId(commonUtil.getOperatorId()).build()).getContext().getParentCustomerRelaVOList();
        if (CollectionUtils.isNotEmpty(parentCustomerRelaVOList)) {
            List<String> child = parentCustomerRelaVOList.stream().map(ParentCustomerRelaVO::getCustomerId).distinct().collect(Collectors.toList());
            customerId.addAll(child);
        }
        if (!customerId.contains(returnOrder.getBuyer().getId())) {
            throw new SbcRuntimeException("K-050003");
        }
    }

    /**
     * 获取所有的主子账号的Id
     * @return
     */
    private List<String> getAllCustomerIds() {
        List<String> customerIds = new ArrayList<>();
        customerIds.add(commonUtil.getOperatorId());
        List<ParentCustomerRelaVO> parentCustomerRelaVOList = parentCustomerRelaQueryProvider.findAllByParentId(ParentCustomerRelaListRequest
                .builder().parentId(commonUtil.getOperatorId()).build()).getContext().getParentCustomerRelaVOList();
        if (CollectionUtils.isNotEmpty(parentCustomerRelaVOList)) {
            customerIds.addAll(parentCustomerRelaVOList.stream().map(ParentCustomerRelaVO::getCustomerId).distinct().collect(Collectors.toList()));
        }
        return customerIds;
    }
    /**
     * 校验是否可退
     *
     * @param tid
     */
    private void verifyIsReturnable(String tid) {
        NewPileTradeVO trade =
                newPileTradeProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        commonUtil.checkIfStore(trade.getSupplier().getStoreId());

        if(CollectionUtils.isNotEmpty(trade.getSendCouponCodeIds())) {
            List<CouponCodeDTO> couponCodeList = couponCodeQueryProvider.findCouponsByCouponCodeIds(CouponsByCodeIdsRequest
                    .builder().couponCodeIds(trade.getSendCouponCodeIds()).build()).getContext().getCouponCodeList();
            log.info("=================》退单校验是否赠券，couponCodeList：：：{}",couponCodeList);
            boolean useStatus = couponCodeList.stream().anyMatch(coupon -> coupon.getUseStatus().equals(DefaultFlag.YES));
            log.info("=================》使用状态，useStatus：：：{}",useStatus);
            if (useStatus) {
                throw new SbcRuntimeException("K-050320");
            }
        }

        if (Objects.nonNull(trade.getTradeState().getDeliverStatus()) && (trade.getTradeState().getDeliverStatus() == DeliverStatus.SHIPPED || trade.getTradeState().getDeliverStatus() == DeliverStatus.PART_SHIPPED)) {
            TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
            request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
            TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
            if (config.getStatus() == 0) {
                throw new SbcRuntimeException("K-050208");
            }
            JSONObject content = JSON.parseObject(config.getContext());
            Integer day = content.getObject("day", Integer.class);

            if (Objects.isNull(trade.getTradeState().getEndTime())) {
                throw new SbcRuntimeException("K-050002");
            }
            if (trade.getTradeState().getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < LocalDateTime.now().minusDays(day).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) {
                throw new SbcRuntimeException("K-050208");
            }
        }
    }
}
