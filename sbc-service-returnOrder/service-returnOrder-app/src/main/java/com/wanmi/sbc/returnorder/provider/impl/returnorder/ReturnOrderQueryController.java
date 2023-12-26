package com.wanmi.sbc.returnorder.provider.impl.returnorder;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListByIdsRequest;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByIdResponse;
import com.wanmi.sbc.customer.bean.vo.EmployeeListByIdsVO;
import com.wanmi.sbc.returnorder.api.provider.returnorder.ReturnOrderQueryProvider;
import com.wanmi.sbc.returnorder.api.request.returnorder.*;
import com.wanmi.sbc.returnorder.api.request.trade.TradeCanReturnNumRequest;
import com.wanmi.sbc.returnorder.api.response.returnorder.*;
import com.wanmi.sbc.returnorder.bean.enums.ReturnReason;
import com.wanmi.sbc.returnorder.bean.enums.TradeActivityTypeEnum;
import com.wanmi.sbc.returnorder.bean.vo.ReturnOrderNewVO;
import com.wanmi.sbc.returnorder.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.inventorydetailsamount.service.InventoryDetailSamountService;
import com.wanmi.sbc.returnorder.returnorder.model.entity.ReturnItem;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.request.ReturnQueryRequest;
import com.wanmi.sbc.returnorder.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.returnorder.returnorder.service.newPileOrder.NewPileReturnOrderQuery;
import com.wanmi.sbc.returnorder.trade.model.root.ProviderTrade;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.service.ProviderTradeService;
import com.wanmi.sbc.returnorder.trade.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>退单服务查询接口</p>
 *
 * @Author: daiyitian
 * @Description: 退单服务查询接口
 * @Date: 2018-12-03 15:40
 */
@Validated
@RestController
@Slf4j
public class ReturnOrderQueryController implements ReturnOrderQueryProvider {

    @Autowired
    private ReturnOrderService returnOrderService;

    @Autowired
    private ProviderTradeService providerTradeService;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private NewPileReturnOrderQuery newPileReturnOrderQuery;

    @Autowired
    InventoryDetailSamountService inventoryDetailSamountService;

    @Autowired
    TradeService tradeService;

    /**
     * 根据userId获取退单快照
     *
     * @param request 根据userId获取退单快照请求结构 {@link ReturnOrderTransferByUserIdRequest}
     * @return 退单快照 {@link ReturnOrderTransferByUserIdResponse}
     */
    @Override
    public BaseResponse<ReturnOrderTransferByUserIdResponse> getTransferByUserId(@RequestBody @Valid
                                                                                 ReturnOrderTransferByUserIdRequest
                                                                                         request) {
        ReturnOrder returnOrder = returnOrderService.findTransfer(request.getUserId());
        if (Objects.nonNull(returnOrder)) {
            return BaseResponse.success(KsBeanUtil.convert(returnOrder, ReturnOrderTransferByUserIdResponse.class));
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 根据动态条件统计退单
     *
     * @param request 根据动态条件统计退单请求结构 {@link ReturnCountByConditionRequest}
     * @return 退单数 {@link ReturnCountByConditionResponse}
     */
    @Override
    public BaseResponse<ReturnCountByConditionResponse> countByCondition(@RequestBody @Valid
                                                                         ReturnCountByConditionRequest
                                                                                 request) {
        Long count = returnOrderService.countNum(KsBeanUtil.convert(request, ReturnQueryRequest.class));
        return BaseResponse.success(ReturnCountByConditionResponse.builder().count(count).build());
    }

    /**
     * 根据动态条件查询退单分页列表
     *
     * @param request 根据动态条件查询退单分页列表请求结构 {@link ReturnOrderPageRequest}
     * @return 退单分页列表 {@link ReturnOrderPageResponse}
     */
    @Override
    public BaseResponse<ReturnOrderPageResponse> page(@RequestBody @Valid ReturnOrderPageRequest request) {
        Page<ReturnOrder> orderPage = returnOrderService.page(KsBeanUtil.convert(request, ReturnQueryRequest.class));
        MicroServicePage<ReturnOrderVO> returnOrderVOS = KsBeanUtil.convertPage(orderPage, ReturnOrderVO.class);
        //查询业务员信息
        // List<EmployeeListVO> employeeList = employeeQueryProvider.list(new EmployeeListRequest()).getContext().getEmployeeList();
        List<ReturnOrderVO> list = returnOrderVOS.getContent();
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, String> employeeNameMap = null;
            List<String> employeeIds = list.stream()
                    .filter(o -> Objects.nonNull(o.getBuyer()) && StringUtils.isNotBlank(o.getBuyer().getEmployeeId()))
                    .map(o -> o.getBuyer().getEmployeeId()).distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(employeeIds)) {
                List<EmployeeListByIdsVO> employeeVoList = employeeQueryProvider.listByIds(EmployeeListByIdsRequest.builder().employeeIds(employeeIds).build()).getContext().getEmployeeList();
                if (CollectionUtils.isNotEmpty(employeeVoList)) {
                    employeeNameMap = employeeVoList.stream().filter(o -> Objects.nonNull(o.getEmployeeName())).collect(Collectors.toMap(EmployeeListByIdsVO::getEmployeeId, EmployeeListByIdsVO::getEmployeeName));
                }
            }
            for (ReturnOrderVO returnOrderVO : list) {
                if (Objects.nonNull(returnOrderVO.getBuyer())
                        && StringUtils.isNotBlank(returnOrderVO.getBuyer().getEmployeeId())
                        && Objects.nonNull(employeeNameMap)) {
                    returnOrderVO.setEmployeeName(employeeNameMap.get(returnOrderVO.getBuyer().getEmployeeId()));
                }
                if (returnOrderVO.getPtid() != null) {
                    ProviderTrade providerTrade = providerTradeService.findbyId(returnOrderVO.getPtid());
                    TradeVO tradeVO = KsBeanUtil.convert(providerTrade, TradeVO.class);
                    if (Objects.nonNull(tradeVO) && StringUtils.isNotBlank(tradeVO.getBuyer().getEmployeeId())) {
                        EmployeeByIdResponse employee = employeeQueryProvider.getById(EmployeeByIdRequest.builder().employeeId(tradeVO.getBuyer().getEmployeeId()).build()).getContext();
                        if (Objects.nonNull(employee)) {
                            tradeVO.setEmployeeName(employee.getEmployeeName());
                        }
                    }
                    returnOrderVO.setTradeVO(tradeVO);
                }
            }
        }

        returnOrderVOS.setContent(list);
        return BaseResponse.success(ReturnOrderPageResponse.builder()
                .returnOrderPage(returnOrderVOS).build());
    }

    /**
     * 根据动态条件查询退单分页列表（新）
     *
     * @param request 根据动态条件查询退单分页列表请求结构 {@link ReturnOrderPageRequest}
     * @return 退单分页列表 {@link ReturnOrderPageNewResponse}
     */
    @Override
    public BaseResponse<ReturnOrderPageNewResponse> newPage(ReturnOrderPageRequest request) {
        Page<ReturnOrder> orderPage = returnOrderService.page(KsBeanUtil.convert(request, ReturnQueryRequest.class));

        MicroServicePage<ReturnOrderNewVO> returnOrderVOS = KsBeanUtil.convertPage(orderPage, ReturnOrderNewVO.class);
        List<ReturnOrderNewVO> list = returnOrderVOS.getContent();
        for (int i = 0; i < list.size(); i++) {
            //查询订单相关的状态
            ProviderTrade providerTrade = providerTradeService.findbyId(list.get(i).getTid());
            list.get(i).setCanReturnFlag(providerTrade.getCanReturnFlag());
            list.get(i).setIsSelf(providerTrade.getSupplier().getIsSelf());
            list.get(i).setOrderSource(providerTrade.getOrderSource());
            //退单id
            String id = list.get(i).getId();
            ReturnOrder returnOrder = orderPage.stream().filter(op -> op.getId().equals(id)).findFirst().orElse(null);
            if (Objects.nonNull(returnOrder)) {
                list.get(i).setReturnItemPics(returnOrder.getReturnItems().stream().map(ReturnItem::getPic).collect(Collectors.toList()));
                list.get(i).setReturnOrderNum(returnOrder.getReturnItems().stream().map(ReturnItem::getNum).reduce(BigDecimal.ZERO, BigDecimal::add).intValue());

            }
        }
        ;

        returnOrderVOS.setContent(list);
        return BaseResponse.success(ReturnOrderPageNewResponse.builder()
                .returnOrderPage(returnOrderVOS).build());
    }

    /**
     * 根据动态条件查询退单列表
     *
     * @param request 根据动态条件查询退单列表请求结构 {@link ReturnOrderByConditionRequest}
     * @return 退单列表 {@link ReturnOrderByConditionResponse}
     */
    @Override
    public BaseResponse<ReturnOrderByConditionResponse> listByCondition(@RequestBody @Valid
                                                                        ReturnOrderByConditionRequest
                                                                                request) {
        List<ReturnOrder> orderList = returnOrderService.findByCondition(KsBeanUtil.convert(request,
                ReturnQueryRequest.class));
        List<ReturnOrderVO> orderVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(orderList)) {
            orderVOList = KsBeanUtil.convert(orderList, ReturnOrderVO.class);
        }
        return BaseResponse.success(ReturnOrderByConditionResponse.builder().returnOrderList(orderVOList).build());
    }

    /**
     * 根据id查询退单
     *
     * @param request 根据id查询退单请求结构 {@link ReturnOrderByIdRequest}
     * @return 退单信息 {@link ReturnOrderByIdResponse}
     */
    @Override
    public BaseResponse<ReturnOrderByIdResponse> getById(@RequestBody @Valid ReturnOrderByIdRequest
                                                                 request) {
        ReturnOrder returnOrder = returnOrderService.findById(request.getRid());
        ProviderTrade providerTrade = providerTradeService.findbyId(returnOrder.getPtid());
        returnOrder.setTradeVO(KsBeanUtil.convert(providerTrade, TradeVO.class));

        Trade trade = tradeService.getById(returnOrder.getTid());
        if (TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())) {
            //拆分returnGoodsList,计算实付金额
            returnOrderService.fillReturnGoodsList(returnOrder);
        }
        if(returnOrder.getReturnOrderAddressVO()==null){
            returnOrderService.setReturnOrderAddress(returnOrder,trade);
        }
        ReturnOrderByIdResponse response = KsBeanUtil.convert(returnOrder, ReturnOrderByIdResponse.class);
        return BaseResponse.success(response);
    }

    /**
     * 查询所有退货方式
     *
     * @return 退货方式列表 {@link ReturnWayListResponse}
     */
    @Override
    public BaseResponse<ReturnWayListResponse> listReturnWay() {
        return BaseResponse.success(ReturnWayListResponse.builder().returnWayList(returnOrderService.findReturnWay())
                .build());
    }

    /**
     * 查询所有退货原因
     *
     * @return 退货原因列表 {@link ReturnReasonListResponse}
     */
    @Override
    public BaseResponse<ReturnReasonListResponse> listReturnReason() {
        List<ReturnReason> returnReasons = returnOrderService.findReturnReason();
        returnReasons = returnReasons.stream().filter(returnReason -> returnReason.getType() != ReturnReason.WMSAUTOMATICRETURN.getType()).collect(Collectors.toList());
        return BaseResponse.success(ReturnReasonListResponse.builder()
                .returnReasonList(returnReasons).build());
    }

    /**
     * 查询可退金额
     *
     * @param request 查询可退金额请求结构 {@link ReturnOrderQueryRefundPriceRequest}
     * @return 可退金额 {@link ReturnOrderQueryRefundPriceResponse}
     */
    @Override
    public BaseResponse<ReturnOrderQueryRefundPriceResponse> queryRefundPrice(@RequestBody @Valid
                                                                                      ReturnOrderQueryRefundPriceRequest
                                                                                      request) {
        return BaseResponse.success(ReturnOrderQueryRefundPriceResponse.builder()
                .refundPrice(returnOrderService.queryRefundPrice(request.getRid())).build());
    }

    @Override
    public BaseResponse<ReturnOrderQueryRefundPriceResponse> queryRefundPriceNewPile(@Valid ReturnOrderQueryRefundPriceRequest request) {
        return BaseResponse.success(ReturnOrderQueryRefundPriceResponse.builder()
                .refundPrice(newPileReturnOrderQuery.findByIdNewPile(request.getRid()).getReturnPrice().getTotalPrice()).build());
    }

    /**
     * 根据订单id查询含可退商品的交易信息
     *
     * @param request 根据订单id查询可退商品数请求结构 {@link CanReturnItemNumByTidRequest}
     * @return 含可退商品的交易信息 {@link CanReturnItemNumByTidResponse}
     */
    @Override
    public BaseResponse<CanReturnItemNumByTidResponse> queryCanReturnItemNumByTid(@RequestBody @Valid
                                                                                          CanReturnItemNumByTidRequest
                                                                                          request) {
        return BaseResponse.success(KsBeanUtil.convert(returnOrderService.queryCanReturnItemNumByTid(request.getTid()),
                CanReturnItemNumByTidResponse.class));
    }

    /**
     * 根据拆箱订单id查询含可退商品的交易信息
     *
     * @param request 根据订单id查询可退商品数请求结构 {@link CanReturnItemNumByTidRequest}
     * @return 含可退商品的交易信息 {@link CanReturnItemNumByTidResponse}
     */
    @Override
    public BaseResponse<CanReturnItemNumByTidResponse> queryCanReturnDevanningItemNumByTid(@RequestBody @Valid
                                                                                                   CanReturnItemNumByTidRequest
                                                                                                   request) {
        return BaseResponse.success(KsBeanUtil.convert(returnOrderService.queryCanReturnDevanningItemNumByTid(request.getTid()),
                CanReturnItemNumByTidResponse.class));
    }

    @Override
    public BaseResponse<CanReturnItemResponse> canReturnFlag(@RequestBody @Valid TradeCanReturnNumRequest request) {
        return BaseResponse.success(new CanReturnItemResponse(returnOrderService
                .removeSpecial(KsBeanUtil.convert(request.getTrade(), Trade.class))));
    }

    /**
     * 根据退单id查询含可退商品的退单信息
     *
     * @param request 根据退单id查询可退商品数请求结构 {@link CanReturnItemNumByIdRequest}
     * @return 含可退商品的退单信息 {@link CanReturnItemNumByIdResponse}
     */
    @Override
    public BaseResponse<CanReturnItemNumByIdResponse> queryCanReturnItemNumById(@RequestBody @Valid
                                                                                        CanReturnItemNumByIdRequest
                                                                                        request) {
        return BaseResponse.success(KsBeanUtil.convert(returnOrderService.queryCanReturnItemNumById(request.getRid()),
                CanReturnItemNumByIdResponse.class));
    }

    /**
     * 根据订单id查询退单列表(不包含已作废状态以及拒绝收货的退货单与拒绝退款的退款单)
     *
     * @param request 查询退单列表请求结构 {@link ReturnOrderNotVoidByTidRequest}
     * @return 退单列表 {@link ReturnOrderNotVoidByTidResponse}
     */
    @Override
    public BaseResponse<ReturnOrderNotVoidByTidResponse> listNotVoidByTid(@RequestBody @Valid
                                                                                  ReturnOrderNotVoidByTidRequest
                                                                                  request) {
        List<ReturnOrder> orders = returnOrderService.findReturnsNotVoid(request.getTid());
        return BaseResponse.success(ReturnOrderNotVoidByTidResponse.builder()
                .returnOrderList(KsBeanUtil.convert(orders, ReturnOrderVO.class)).build());
    }

    /**
     * 根据订单id查询所有退单
     *
     * @param request 根据订单id查询请求结构 {@link ReturnOrderListByTidRequest}
     * @return 退单列表 {@link ReturnOrderListByTidResponse}
     */
    @Override
    public BaseResponse<ReturnOrderListByTidResponse> listByTid(@RequestBody @Valid ReturnOrderListByTidRequest
                                                                        request) {
        List<ReturnOrder> orderList = returnOrderService.findReturnByTid(request.getTid());
        List<ReturnOrderVO> orderVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(orderList)) {
            orderVOList = KsBeanUtil.convert(orderList, ReturnOrderVO.class);
        }
        return BaseResponse.success(ReturnOrderListByTidResponse.builder().returnOrderList(orderVOList).build());
    }

    /**
     * 根据结束时间统计退单
     *
     * @param request 根据结束时间统计退单请求结构 {@link ReturnOrderCountByEndDateRequest}
     * @return 退单数 {@link ReturnOrderCountByEndDateResponse}
     */
    @Override
    public BaseResponse<ReturnOrderCountByEndDateResponse> countByEndDate(@RequestBody @Valid
                                                                                  ReturnOrderCountByEndDateRequest
                                                                                  request) {
        return BaseResponse.success(ReturnOrderCountByEndDateResponse.builder()
                .count(returnOrderService.countReturnOrderByEndDate(request.getEndDate(), request.getReturnFlowState()))
                .build());
    }


    /**
     * 根据结束时间查询退单列表
     *
     * @param request 根据结束时间查询退单列表请求结构 {@link ReturnOrderListByEndDateRequest}
     * @return 退单列表 {@link ReturnOrderListByEndDateResponse}
     */
    @Override
    public BaseResponse<ReturnOrderListByEndDateResponse> listByEndDate(@RequestBody @Valid
                                                                                ReturnOrderListByEndDateRequest
                                                                                request) {
        List<ReturnOrder> orderList = returnOrderService.queryReturnOrderByEndDate(request.getEndDate(),
                request.getStart(), request.getEnd(), request.getReturnFlowState());
        List<ReturnOrderVO> orderVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(orderList)) {
            orderVOList = KsBeanUtil.convert(orderList, ReturnOrderVO.class);
        }
        return BaseResponse.success(ReturnOrderListByEndDateResponse.builder().returnOrderList(orderVOList).build());
    }

    /**
     * 根据状态统计退单
     *
     * @param request 根据状态统计退单请求结构 {@link ReturnOrderCountByFlowStateRequest}
     * @return 退单数 {@link ReturnOrderCountByFlowStateResponse}
     */
//    @Override
//    public BaseResponse<ReturnOrderCountByFlowStateResponse> countByFlowState(@RequestBody @Valid
//                                                                                      ReturnOrderCountByFlowStateRequest
//                                                                                      request) {
//        ReturnOrderTodoReponse todoReponse = returnOrderService.countReturnOrderByFlowState(
//                KsBeanUtil.convert(request, ReturnQueryRequest.class));
//        return BaseResponse.success(KsBeanUtil.convert(todoReponse, ReturnOrderCountByFlowStateResponse.class));
//    }
}
