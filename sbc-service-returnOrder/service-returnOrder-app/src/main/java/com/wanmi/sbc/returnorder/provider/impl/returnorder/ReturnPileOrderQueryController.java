package com.wanmi.sbc.returnorder.provider.impl.returnorder;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListRequest;
import com.wanmi.sbc.customer.bean.vo.EmployeeListVO;
import com.wanmi.sbc.returnorder.api.provider.returnorder.ReturnPileOrderQueryProvider;
import com.wanmi.sbc.returnorder.api.request.trade.TradeCanReturnNumRequest;
import com.wanmi.sbc.returnorder.bean.enums.ReturnReason;
import com.wanmi.sbc.returnorder.bean.vo.ReturnOrderNewVO;
import com.wanmi.sbc.returnorder.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.returnorder.model.entity.ReturnItem;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.returnorder.returnorder.request.ReturnQueryRequest;
import com.wanmi.sbc.returnorder.returnorder.service.ReturnPileOrderService;
import com.wanmi.sbc.returnorder.trade.model.root.PileTrade;
import com.wanmi.sbc.returnorder.trade.model.root.ProviderTrade;
import com.wanmi.sbc.returnorder.trade.service.ProviderTradeService;
import com.wanmi.sbc.returnorder.api.request.returnorder.*;
import com.wanmi.sbc.returnorder.api.response.returnorder.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 退单服务查询接口
 * @author: jiangxin
 * @create: 2021-09-28 20:07
 */
@Validated
@RestController
public class ReturnPileOrderQueryController implements ReturnPileOrderQueryProvider {
    @Autowired
    private ReturnPileOrderService returnPileOrderService;

    @Autowired
    private ProviderTradeService providerTradeService;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

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
        ReturnPileOrder returnPileOrder = returnPileOrderService.findTransfer(request.getUserId());
        if (Objects.nonNull(returnPileOrder)) {
            return BaseResponse.success(KsBeanUtil.convert(returnPileOrder, ReturnOrderTransferByUserIdResponse.class));
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
        Long count = returnPileOrderService.countNum(KsBeanUtil.convert(request, ReturnQueryRequest.class));
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
        Page<ReturnPileOrder> orderPage = returnPileOrderService.page(KsBeanUtil.convert(request, ReturnQueryRequest.class));
        MicroServicePage<ReturnOrderVO> returnOrderVOS = KsBeanUtil.convertPage(orderPage, ReturnOrderVO.class);
        //查询业务员信息
        List<EmployeeListVO> employeeList = employeeQueryProvider.list(new EmployeeListRequest()).getContext().getEmployeeList();
        List<ReturnOrderVO> list=returnOrderVOS.getContent();
        for(int i=0;i<list.size();i++){
            for (EmployeeListVO employeeListVO: employeeList) {
                if(employeeListVO.getEmployeeId().equals(list.get(i).getBuyer().getEmployeeId())){
                    list.get(i).setEmployeeName(employeeListVO.getEmployeeName());
                    break;
                }
            }
            if (list.get(i).getPtid()!=null){
                ProviderTrade providerTrade=providerTradeService.findbyId(list.get(i).getPtid());
                TradeVO tradeVO = KsBeanUtil.convert(providerTrade, TradeVO.class);
                for (EmployeeListVO employeeListVO: employeeList) {
                    if(tradeVO.getBuyer().getEmployeeId().equals(employeeListVO.getEmployeeId())){
                        tradeVO.setEmployeeName(employeeListVO.getEmployeeName());
                        break;
                    }
                }
                list.get(i).setTradeVO(tradeVO);
            }
        };
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
        Page<ReturnPileOrder> orderPage = returnPileOrderService.page(KsBeanUtil.convert(request, ReturnQueryRequest.class));

        MicroServicePage<ReturnOrderNewVO> returnOrderVOS = KsBeanUtil.convertPage(orderPage, ReturnOrderNewVO.class);
        List<ReturnOrderNewVO> list=returnOrderVOS.getContent();
        for(int i=0;i<list.size();i++){
            //查询订单相关的状态
            ProviderTrade providerTrade = providerTradeService.findbyId(list.get(i).getTid());
            list.get(i).setCanReturnFlag(providerTrade.getCanReturnFlag());
            list.get(i).setIsSelf(providerTrade.getSupplier().getIsSelf());
            list.get(i).setOrderSource(providerTrade.getOrderSource());
            //退单id
            String id = list.get(i).getId();
            ReturnPileOrder returnPileOrder = orderPage.stream().filter(op -> op.getId().equals(id)).findFirst().orElse(null);
            if(Objects.nonNull(returnPileOrder)){
                list.get(i).setReturnItemPics(returnPileOrder.getReturnItems().stream().map(ReturnItem::getPic).collect(Collectors.toList()));
                list.get(i).setReturnOrderNum(returnPileOrder.getReturnItems().stream().map(ReturnItem::getNum).reduce(BigDecimal.ZERO, BigDecimal::add).intValue());

            }
        };

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
        List<ReturnPileOrder> orderList = returnPileOrderService.findByCondition(KsBeanUtil.convert(request,
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
        ReturnPileOrder returnPileOrder=returnPileOrderService.findById(request.getRid());
        ProviderTrade providerTrade=providerTradeService.findbyId(returnPileOrder.getPtid());
        returnPileOrder.setTradeVO(KsBeanUtil.convert(providerTrade,TradeVO.class));
        return BaseResponse.success(KsBeanUtil.convert(returnPileOrder, ReturnOrderByIdResponse.class));
    }

    /**
     * 查询所有退货方式
     *
     * @return 退货方式列表 {@link ReturnWayListResponse}
     */
    @Override
    public BaseResponse<ReturnWayListResponse> listReturnWay() {
        return BaseResponse.success(ReturnWayListResponse.builder().returnWayList(returnPileOrderService.findReturnWay())
                .build());
    }

    /**
     * 查询所有退货原因
     *
     * @return 退货原因列表 {@link ReturnReasonListResponse}
     */
    @Override
    public BaseResponse<ReturnReasonListResponse> listReturnReason() {
        List<ReturnReason> returnReasons = returnPileOrderService.findReturnReason();
        returnReasons=returnReasons.stream().filter(returnReason -> returnReason.getType()!=ReturnReason.WMSAUTOMATICRETURN.getType()).collect(Collectors.toList());
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
                .refundPrice(returnPileOrderService.queryRefundPrice(request.getRid())).build());
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
        return BaseResponse.success(KsBeanUtil.convert(returnPileOrderService.queryCanReturnItemNumByTid(request.getTid()),
                CanReturnItemNumByTidResponse.class));
    }

    @Override
    public BaseResponse<CanReturnItemNumByTidResponse> queryFilterCanReturnItemsByTid(CanReturnItemNumByTidRequest request) {
        return BaseResponse.success(KsBeanUtil.convert(returnPileOrderService.queryFilterCanReturnItemsByTid(request.getTid()),
                CanReturnItemNumByTidResponse.class));
    }

    @Override
    public BaseResponse<CanReturnItemResponse> canReturnFlag(@RequestBody @Valid TradeCanReturnNumRequest request) {
        return BaseResponse.success(new CanReturnItemResponse(returnPileOrderService
                .removeSpecial(KsBeanUtil.convert(request.getTrade(), PileTrade.class))));
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
        return BaseResponse.success(KsBeanUtil.convert(returnPileOrderService.queryCanReturnItemNumById(request.getRid()),
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
        List<ReturnPileOrder> orders = returnPileOrderService.findReturnsNotVoid(request.getTid());
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
        List<ReturnPileOrder> orderList = returnPileOrderService.findReturnByTid(request.getTid());
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
                .count(returnPileOrderService.countReturnOrderByEndDate(request.getEndDate(), request.getReturnFlowState()))
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
        List<ReturnPileOrder> orderList = returnPileOrderService.queryReturnOrderByEndDate(request.getEndDate(),
                request.getStart(), request.getEnd(), request.getReturnFlowState());
        List<ReturnOrderVO> orderVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(orderList)) {
            orderVOList = KsBeanUtil.convert(orderList, ReturnOrderVO.class);
        }
        return BaseResponse.success(ReturnOrderListByEndDateResponse.builder().returnOrderList(orderVOList).build());
    }
}
