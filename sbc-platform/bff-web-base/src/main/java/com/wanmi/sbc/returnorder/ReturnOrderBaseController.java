package com.wanmi.sbc.returnorder;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.customer.api.provider.parentcustomerrela.ParentCustomerRelaQueryProvider;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaListRequest;
import com.wanmi.sbc.customer.bean.vo.ParentCustomerRelaVO;
import com.wanmi.sbc.order.api.provider.InventoryDetailSamount.InventoryDetailSamountProvider;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.InventoryDetailSamount.InventoryDetailSamountRequest;
import com.wanmi.sbc.order.api.request.refund.RefundOrderResponseByReturnOrderCodeRequest;
import com.wanmi.sbc.order.api.request.returnorder.*;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.response.refund.RefundOrderResponse;
import com.wanmi.sbc.order.bean.dto.ReturnLogisticsDTO;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.bean.enums.ReturnReason;
import com.wanmi.sbc.order.bean.enums.ReturnWay;
import com.wanmi.sbc.order.bean.enums.TradeActivityTypeEnum;
import com.wanmi.sbc.order.bean.vo.*;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sunkun on 2017/7/11.
 */
@Api(tags = "ReturnOrderBaseController", description = "退单基本服务API")
@RestController
@RequestMapping("/return")
public class ReturnOrderBaseController {

    @Autowired
    private ReturnOrderProvider returnOrderProvider;

    @Autowired
    private ReturnOrderQueryProvider returnOrderQueryProvider;

    @Autowired
    private RefundOrderQueryProvider refundOrderQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private ParentCustomerRelaQueryProvider parentCustomerRelaQueryProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private InventoryDetailSamountProvider inventoryDetailSamountProvider;


    /**
     * 分页查询 from ES
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "分页查询 from ES")
    @RequestMapping(value = "page", method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<ReturnOrderVO>> page(@RequestBody ReturnOrderPageRequest request) {

        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            request.setStoreId(domainStoreRelaVO.getStoreId());
        }
        List<String> customerIds = this.getAllCustomerIds();
        request.setCustomerIds(customerIds.toArray());
//        request.setBuyerId(commonUtil.getOperatorId());
        request.setInviteeId(request.getInviteeId());
        request.setChannelType(request.getChannelType());
        MicroServicePage<ReturnOrderVO> returnPage =
                returnOrderQueryProvider.page(request).getContext().getReturnOrderPage();
        return BaseResponse.success(returnPage);
    }

    /**
     * 分页查询 from ES
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "(新)分页查询 from ES")
    @RequestMapping(value = "Page2", method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<ReturnOrderNewVO>> page2(@RequestBody ReturnOrderPageRequest request) {

        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            request.setStoreId(domainStoreRelaVO.getStoreId());
        }
        List<String> customerIds = this.getAllCustomerIds();
        request.setCustomerIds(customerIds.toArray());
//        request.setBuyerId(commonUtil.getOperatorId());
        request.setInviteeId(request.getInviteeId());
        request.setChannelType(request.getChannelType());
        MicroServicePage<ReturnOrderNewVO> returnOrderPage = returnOrderQueryProvider.newPage(request).getContext().getReturnOrderPage();
        return BaseResponse.success(returnOrderPage);
    }

    /**
     * 查看退单详情
     *
     * @param rid
     * @return
     */
    @ApiOperation(value = "查看退单详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/{rid}")
    public BaseResponse<ReturnOrderVO> findById(@PathVariable String rid) {
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build())
                .getContext();
        checkUnauthorized(rid, returnOrder);
        return BaseResponse.success(returnOrder);
    }

    /**
     * 查看退单附件
     *
     * @param rid
     * @return
     */
    @ApiOperation(value = "查看退单附件")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/images/{rid}", method = RequestMethod.GET)
    public BaseResponse<List<String>> images(@PathVariable String rid) {
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build())
                .getContext();
        checkUnauthorized(rid, returnOrder);
        return BaseResponse.success(returnOrder.getImages());
    }

    /**
     * 查看退单商品清单
     *
     * @param rid
     * @return
     */
    @ApiOperation(value = "查看退单商品清单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/returnItems/{rid}", method = RequestMethod.GET)
    public BaseResponse<List<ReturnItemVO>> returnItems(@PathVariable String rid) {
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build())
                .getContext();
        checkUnauthorized(rid, returnOrder);
        return BaseResponse.success(returnOrder.getReturnItems());
    }

    /**
     * 查询退款物流
     *
     * @param rid
     * @return
     */
    @ApiOperation(value = "查询退款物流")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/returnLogistics/{rid}", method = RequestMethod.GET)
    public BaseResponse<ReturnLogisticsVO> returnLogistics(@PathVariable String rid) {
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build())
                .getContext();
        checkUnauthorized(rid, returnOrder);
        return BaseResponse.success(returnOrder.getReturnLogistics());
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
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build())
                .getContext();
        checkUnauthorized(rid, returnOrder);
        RefundOrderResponse refundOrderResponse =
                refundOrderQueryProvider.getRefundOrderRespByReturnOrderCode(new RefundOrderResponseByReturnOrderCodeRequest(rid)).getContext();
        return BaseResponse.success(refundOrderResponse);

    }


    /**
     * 查找所有退货方式
     *
     * @return
     */
    @ApiOperation(value = "查找所有退货方式")
    @RequestMapping(value = "/ways", method = RequestMethod.GET)
    public BaseResponse<List<ReturnWay>> findReturnWay() {
        return BaseResponse.success(returnOrderQueryProvider.listReturnWay().getContext().getReturnWayList());
    }

    /**
     * 所有退货原因
     *
     * @return
     */
    @ApiOperation(value = "所有退货原因")
    @RequestMapping(value = "/reasons", method = RequestMethod.GET)
    public BaseResponse<List<ReturnReason>> findReturnReason() {
        return BaseResponse.success(returnOrderQueryProvider.listReturnReason().getContext().getReturnReasonList());
    }


    /**
     * 查看退货订单详情和可退商品数
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "查看退货订单详情和可退商品数")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单Id", required = true)
    @RequestMapping(value = "/trade/{tid}", method = RequestMethod.GET)
    public BaseResponse<TradeVO> tradeDetails(@PathVariable String tid) {
        TradeVO trade = returnOrderQueryProvider.queryCanReturnItemNumByTid(CanReturnItemNumByTidRequest.builder()
                .tid(tid).build()).getContext();
//        if (!trade.getBuyer().getId().equals(commonUtil.getOperatorId())) {
//            throw new SbcRuntimeException("K-050100", new Object[]{tid});
//        }
//        commonUtil.checkIfStore(trade.getSupplier().getStoreId());
        return BaseResponse.success(trade);
    }

    /**
     * 拆箱订单查看退货订单详情和可退商品数
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "拆箱订单查看退货订单详情和可退商品数")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单Id", required = true)
    @RequestMapping(value = "/devanning/trade/{tid}", method = RequestMethod.GET)
    public BaseResponse<TradeVO> devanningTradeDetails(@PathVariable String tid) {
        TradeVO trade = returnOrderQueryProvider.queryCanReturnDevanningItemNumByTid(CanReturnItemNumByTidRequest.builder()
                .tid(tid).build()).getContext();
//        if (!trade.getBuyer().getId().equals(commonUtil.getOperatorId())) {
//            throw new SbcRuntimeException("K-050100", new Object[]{tid});
//        }
//        commonUtil.checkIfStore(trade.getSupplier().getStoreId());
        return BaseResponse.success(trade);
    }

    /**
     * 根据订单id查询已完成的退单
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "根据订单id查询已完成的退单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单Id", required = true)
    @RequestMapping(value = "/findCompletedByTid/{tid}", method = RequestMethod.GET)
    public BaseResponse<List<ReturnOrderVO>> findCompletedByTid(@PathVariable String tid) {
        String customerId = commonUtil.getOperatorId();
        List<ReturnOrderVO> returnOrders = returnOrderQueryProvider.listNotVoidByTid(ReturnOrderNotVoidByTidRequest
                .builder().tid(tid).build()).getContext().getReturnOrderList();
        if (returnOrders.stream().anyMatch(r -> !r.getBuyer().getId().equals(customerId))) {
            throw new SbcRuntimeException("K-050003");
        }

        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            if (returnOrders.stream().anyMatch(r -> !r.getCompany().getStoreId().equals(domainStoreRelaVO.getStoreId()))) {
                throw new SbcRuntimeException("K-050003");
            }
        }
        return BaseResponse.success(returnOrders);
    }

    /**
     * 填写物流信息
     *
     * @param rid
     * @param logistics
     * @return
     */
    @ApiOperation(value = "填写物流信息")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/deliver/{rid}", method = RequestMethod.POST)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse deliver(@PathVariable String rid, @RequestBody ReturnLogisticsDTO logistics) {
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build())
                .getContext();
        checkUnauthorized(rid, returnOrder);
        return returnOrderProvider.deliver(ReturnOrderDeliverRequest.builder().rid(rid).logistics(logistics)
                .operator(commonUtil.getOperator()).build());
    }

    /**
     * 填写物流信息(ios专属)
     * @param logistics
     * @return
     */
    @ApiOperation(value = "填写物流信息（ios专用）")
    @RequestMapping(value = "/deliver2", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse deliver2(@RequestBody ReturnLogisticsDTO logistics) {

        if(Objects.isNull(logistics.getRid())){
            throw new SbcRuntimeException("K-000009");
        }
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(logistics.getRid()).build())
                .getContext();
        checkUnauthorized(logistics.getRid(), returnOrder);
        return returnOrderProvider.deliver(ReturnOrderDeliverRequest.builder().rid(logistics.getRid()).logistics(logistics)
                .operator(commonUtil.getOperator()).build());
    }


    /**
     * 取消退单
     *
     * @param rid
     * @param reason
     * @return
     */
    @ApiOperation(value = "取消退单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "rid", value = "退单Id", required = true)
    @RequestMapping(value = "/cancel/{rid}", method = RequestMethod.POST)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse cancel(@PathVariable String rid, @RequestParam("reason") String reason) {
        ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build())
                .getContext();
        checkUnauthorized(rid, returnOrder);
        if (returnOrder.getReturnFlowState() != ReturnFlowState.INIT) {
            throw new SbcRuntimeException("K-050102");
        }
        return returnOrderProvider.cancel(ReturnOrderCancelRequest.builder().rid(rid).remark(reason)
                .operator(commonUtil.getOperator()).build());
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
//        String customerId = commonUtil.getOperatorId();
        List<ReturnOrderVO> returnOrders = returnOrderQueryProvider.listByTid(ReturnOrderListByTidRequest.builder()
                .tid(tid).build()).getContext().getReturnOrderList();
        TradeVO tradeVO = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        List<TradeItemVO> collect = tradeVO.getTradeItems().stream().filter(param -> Objects.nonNull(param.getGoodsInfoType()) && param.getGoodsInfoType() == 1).collect(Collectors.toList());
        if (collect.size()==tradeVO.getTradeItems().size()){
            throw new SbcRuntimeException("K-050138");
        }
//        if (returnOrders.stream().anyMatch(r -> !r.getBuyer().getId().equals(customerId))) {
//            throw new SbcRuntimeException("K-050003");
//        }
//        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
//        if (Objects.nonNull(domainStoreRelaVO)) {
//            if (returnOrders.stream().anyMatch(r -> !r.getCompany().getStoreId().equals(domainStoreRelaVO.getStoreId()))) {
//                throw new SbcRuntimeException("K-050003");
//            }
//        }
        return BaseResponse.success(returnOrders.stream().filter(o -> o.getReturnFlowState() != ReturnFlowState.REJECT_REFUND &&
                o.getReturnFlowState() != ReturnFlowState.REJECT_RECEIVE && o.getReturnFlowState() != ReturnFlowState.VOID).collect(Collectors.toList()));
    }

    private void checkUnauthorized(@PathVariable String rid, ReturnOrderVO returnOrder) {
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
//        commonUtil.checkIfStore(returnOrder.getCompany().getStoreId());
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
}
