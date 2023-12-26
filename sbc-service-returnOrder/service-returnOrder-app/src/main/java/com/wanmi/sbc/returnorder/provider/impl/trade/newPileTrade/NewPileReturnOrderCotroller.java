package com.wanmi.sbc.returnorder.provider.impl.trade.newPileTrade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListRequest;
import com.wanmi.sbc.customer.bean.vo.CustomerAccountVO;
import com.wanmi.sbc.customer.bean.vo.EmployeeListVO;
import com.wanmi.sbc.returnorder.api.provider.trade.newPileTrade.NewPileReturnOrderProvider;
import com.wanmi.sbc.returnorder.api.request.refund.RefundOrderByReturnOrderNoRequest;
import com.wanmi.sbc.returnorder.api.request.refund.RefundOrderResponseByReturnOrderCodeRequest;
import com.wanmi.sbc.returnorder.api.request.trade.newpile.RefreshReturnedOrderRequest;
import com.wanmi.sbc.returnorder.api.response.refund.RefundOrderByReturnOrderNoResponse;
import com.wanmi.sbc.returnorder.api.response.refund.RefundOrderResponse;
import com.wanmi.sbc.returnorder.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.returnorder.refund.model.root.RefundBill;
import com.wanmi.sbc.returnorder.refund.model.root.RefundOrder;
import com.wanmi.sbc.returnorder.refund.service.RefundOrderService;
import com.wanmi.sbc.returnorder.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.returnorder.returnorder.request.NewPileReturnQueryRequest;
import com.wanmi.sbc.returnorder.returnorder.request.ReturnQueryRequest;
import com.wanmi.sbc.returnorder.returnorder.service.newPileOrder.NewPileReturnOrderService;
import com.wanmi.sbc.returnorder.api.request.returnorder.*;
import com.wanmi.sbc.returnorder.api.response.returnorder.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Validated
@RestController
@Slf4j
public class NewPileReturnOrderCotroller implements NewPileReturnOrderProvider {

    @Autowired
    private NewPileReturnOrderService newPileReturnOrderService;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private RefundOrderService refundOrderService;

    /**
     * 根据退单编号查询退款单
     * @param refundOrderResponseByReturnOrderCodeRequest {@link RefundOrderResponseByReturnOrderCodeRequest }
     * @return
     */
    @Override
    public BaseResponse<RefundOrderResponse> getRefundOrderReturnOrderCodeNewPile(@Valid RefundOrderResponseByReturnOrderCodeRequest refundOrderResponseByReturnOrderCodeRequest) {
        com.wanmi.sbc.returnorder.bean.vo.RefundOrderResponse refundOrderResponse = newPileReturnOrderService.findByReturnOrderNoNewPile(refundOrderResponseByReturnOrderCodeRequest.getReturnOrderCode());
        return BaseResponse.success(KsBeanUtil.convert(refundOrderResponse,RefundOrderResponse.class));
    }

    /**
     * 根据动态条件查询退单分页列表
     *
     * @param request 根据动态条件查询退单分页列表请求结构 {@link NewPileReturnOrderPageRequest}
     * @return 退单分页列表 {@link ReturnOrderPageResponse}
     */
    @Override
    public BaseResponse<ReturnOrderPageResponse> page(@Valid NewPileReturnOrderPageRequest request) {
        Page<NewPileReturnOrder> orderPage = newPileReturnOrderService.page(KsBeanUtil.convert(request, NewPileReturnQueryRequest.class));
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
        };
        returnOrderVOS.setContent(list);
        return BaseResponse.success(ReturnOrderPageResponse.builder()
                .returnOrderPage(returnOrderVOS).build());
    }

    @Override
    public BaseResponse<RefundOrderByReturnOrderNoResponse> getByReturnOrderNo(@RequestBody @Valid RefundOrderByReturnOrderNoRequest refundOrderByReturnOrderCodeRequest){
        RefundOrder refundOrder = refundOrderService.getRefundOrderByReturnOrderNo(refundOrderByReturnOrderCodeRequest.getReturnOrderCode());
        return BaseResponse.success(KsBeanUtil.convert(refundOrder,RefundOrderByReturnOrderNoResponse.class));
    }

    @Override
    public BaseResponse<RefundOrderResponse> getPileRefundOrderRespByReturnOrderCode(RefundOrderResponseByReturnOrderCodeRequest refundOrderResponseByReturnOrderCodeRequest) {
       com.wanmi.sbc.returnorder.bean.vo.RefundOrderResponse refundOrderResponse = newPileReturnOrderService.generatePileRefundOrderResponse(refundOrderResponseByReturnOrderCodeRequest.getReturnOrderCode());
        return BaseResponse.success(KsBeanUtil.convert(refundOrderResponse,RefundOrderResponse.class));
    }

    @Override
    public BaseResponse<NewPileReturnOrderByIdResponse> getById(@Valid ReturnOrderByIdRequest request) {
        NewPileReturnOrder newPileReturnOrder=newPileReturnOrderService.findReturnPileOrderById(request.getRid());
        return BaseResponse.success(KsBeanUtil.convert(newPileReturnOrder, NewPileReturnOrderByIdResponse.class));
    }

    /**
     * 退单审核
     *
     * @param request 退单审核请求结构 {@link ReturnOrderAuditRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse audit(@RequestBody @Valid ReturnOrderAuditRequest request) {
        newPileReturnOrderService.audit(request.getRid(), request.getOperator());
        return BaseResponse.SUCCESSFUL();
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
        List<ReturnPileOrder> orderList = newPileReturnOrderService.findByCondition(KsBeanUtil.convert(request,
                ReturnQueryRequest.class));
        List<ReturnOrderVO> orderVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(orderList)) {
            orderVOList = KsBeanUtil.convert(orderList, ReturnOrderVO.class);
        }
        return BaseResponse.success(ReturnOrderByConditionResponse.builder().returnOrderList(orderVOList).build());
    }

    /**
     * 退单s2b退单线下退款
     *
     * @param request 平台退单线下退款请求结构 {@link ReturnOrderOfflineRefundForBossRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse offlineRefundForBoss(@RequestBody @Valid ReturnOrderOfflineRefundForBossRequest request) {
        newPileReturnOrderService.s2bBossRefundOffline(request.getRid(), KsBeanUtil.convert(request.getRefundBill(),
                RefundBill.class), request.getOperator(), request.getTid());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<ReturnOrderAddResponse> add(ReturnOrderAddRequest request) {
        String rid = newPileReturnOrderService.create(KsBeanUtil.convert(request.getReturnOrder(), NewPileReturnOrder.class),
                request.getOperator(), request.getForceRefund());
        return BaseResponse.success(ReturnOrderAddResponse.builder().returnOrderId(rid).build());
    }

    /**
     * 根据订单id查询所有退单
     *
     * @param request 根据订单id查询请求结构 {@link ReturnOrderListByTidRequest}
     * @return 退单列表 {@link ReturnOrderListByTidResponse}
     */
    @Override
    public BaseResponse<ReturnOrderListByTidResponse> newPilelistByTid(@RequestBody @Valid ReturnOrderListByTidRequest
                                                                               request) {
        List<NewPileReturnOrder> orderList = newPileReturnOrderService.findReturnByTid(request.getTid());
        List<ReturnOrderVO> orderVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(orderList)) {
            orderVOList = KsBeanUtil.convert(orderList, ReturnOrderVO.class);
        }
        return BaseResponse.success(ReturnOrderListByTidResponse.builder().returnOrderList(orderVOList).build());
    }

    /**
     * 根据拆箱订单id查询含可退商品的交易信息
     *
     * @param request 根据订单id查询可退商品数请求结构 {@link CanReturnItemNumByTidRequest}
     * @return 含可退商品的交易信息 {@link CanReturnItemNumByTidResponse}
     */
    @Override
    public BaseResponse<CanReturnItemNumByTidNewPileResponse> queryCanReturnItemNumByTidNewPile(@RequestBody @Valid
                                                                                                        CanReturnItemNumByTidRequest
                                                                                                        request) {
        return BaseResponse.success(KsBeanUtil.convert(newPileReturnOrderService.queryCanReturnDevanningItemNumByTid(request.getTid()),
                CanReturnItemNumByTidNewPileResponse.class));
    }

    @Override
    public BaseResponse offlineRefundForSupplier(@RequestBody @Valid ReturnOrderOfflineRefundForSupplierRequest request) {
        CustomerAccountVO customerAccount = null;
        if (Objects.nonNull(request.getCustomerAccount())) {
            customerAccount = KsBeanUtil.convert(request.getCustomerAccount(), CustomerAccountVO.class);
        }

        newPileReturnOrderService.offlineRefundForSupplier(request.getRid(),
                customerAccount,
                KsBeanUtil.convert(request.getRefundBill(),
                RefundBill.class), request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse doRefreshReturnOrder(@RequestBody @Valid RefreshReturnedOrderRequest request) {
        newPileReturnOrderService.doRefreshReturnOrder(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<Object> refundOnlineByTid(ReturnOrderOnlineRefundByTidRequest request) {
        newPileReturnOrderService.refundOnlineByTid(request.getReturnOrderCode(), request.getOperator());
        return BaseResponse.SUCCESSFUL();
    }
}
