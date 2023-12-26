package com.wanmi.sbc.trade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.aop.EmployeeCheck;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.SiteResultCode;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListRequest;
import com.wanmi.sbc.customer.bean.vo.EmployeeListVO;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.order.api.provider.payorder.PayOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPilePickTradeItemQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileTradeProvider;
import com.wanmi.sbc.order.api.request.payorder.FindPayOrderRequest;
import com.wanmi.sbc.order.api.request.trade.*;
import com.wanmi.sbc.order.api.request.trade.newpile.FindPickTradeRequest;
import com.wanmi.sbc.order.api.request.trade.newpile.NewPileTradeListByParentIdRequest;
import com.wanmi.sbc.order.api.request.trade.newpile.NewPileTradeListExportRequest;
import com.wanmi.sbc.order.api.request.trade.newpile.NewPileTradePageCriteriaRequest;
import com.wanmi.sbc.order.api.response.payorder.FindPayOrderResponse;
import com.wanmi.sbc.order.api.response.payorder.FindPickTradeResponse;
import com.wanmi.sbc.order.bean.dto.DisabledDTO;
import com.wanmi.sbc.order.bean.dto.NewPileTradeQueryDTO;
import com.wanmi.sbc.order.bean.dto.TradeQueryDTO;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.enums.NewPileFlowState;
import com.wanmi.sbc.order.bean.vo.*;
import com.wanmi.sbc.order.request.DisabledExportRequest;
import com.wanmi.sbc.order.request.NewPileTradeExportRequest;
import com.wanmi.sbc.order.request.TradeExportRequest;
import com.wanmi.sbc.order.service.NewPileTradeExportService;
import com.wanmi.sbc.order.service.ProviderNewPileTradeExportBaseService;
import com.wanmi.sbc.order.service.ProviderTradeExportBaseService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Api(tags = "NewPileTradeController", description = "囤货订单api")
@RestController
@RequestMapping("/supplier/newPileTrade")
@Slf4j
@Validated
public class NewPileTradeController {

    @Autowired
    private NewPileTradeProvider newPileTradeProvider;

    @Autowired
    private NewPilePickTradeItemQueryProvider newPilePickTradeItemQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PayOrderQueryProvider payOrderQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    /**
     * 分页查询supplier
     *
     * @param tradeQueryRequest
     * @return
     */
    @ApiOperation(value = "分页查询")
    @EmployeeCheck
    @RequestMapping(value = "/page",method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<NewPileTradeVO>> supplierPage(@RequestBody NewPileTradeQueryDTO tradeQueryRequest) {
        Long companyInfoId = commonUtil.getCompanyInfoId();
        if (companyInfoId != null) {
            tradeQueryRequest.setSupplierId(companyInfoId);
        }

        if (!tradeQueryRequest.getIsBoss()) {
            if (Objects.nonNull(tradeQueryRequest.getTradeState()) && Objects.nonNull(tradeQueryRequest.getTradeState
                    ().getPayState())) {
                tradeQueryRequest.setNotFlowStates(Arrays.asList(NewPileFlowState.VOID, NewPileFlowState.INIT));
            }
        }
        //设定状态条件逻辑,已审核状态需筛选出已审核与部分发货
//        tradeQueryRequest.makeAllAuditFlow();
        MicroServicePage<NewPileTradeVO> tradePage = newPileTradeProvider.supplierPageCriteria(NewPileTradePageCriteriaRequest.builder().tradePageDTO(tradeQueryRequest).build()).getContext().getTradePage();
        List<NewPileTradeVO> content = tradePage.getContent();
        if(CollectionUtils.isNotEmpty(content)){
            content.forEach(trade->{
                BigDecimal reduce = trade.getTradeItems().stream().map(TradeItemVO::getSplitPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                AtomicReference<BigDecimal> reduce2 = new AtomicReference<>(BigDecimal.ZERO);
                trade.getTradeItems().forEach(item->{
                    List<TradeItemVO.WalletSettlementVo> walletSettlements = item.getWalletSettlements();
                    if(CollectionUtils.isNotEmpty(walletSettlements)){
                        reduce2.set(reduce2.get().add(walletSettlements.stream().map(TradeItemVO.WalletSettlementVo::getReduceWalletPrice).reduce(BigDecimal.ZERO, BigDecimal::add)));
                    }
                });
                trade.getTradePrice().setTotalPrice(reduce.add(reduce2.get()));
            });
        }
        return BaseResponse.success(tradePage);
    }

    /**
     * 查询囤货订单提货记录
     */
    @ApiOperation(value = "查询囤货订单提货记录")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "囤货订单ID", required = true)
    @RequestMapping(value = "/{tid}/pickTrades", method = RequestMethod.GET)
    public BaseResponse<FindPickTradeResponse> findPickTradeList(@PathVariable String tid) {
        NewPileTradeVO trade =
                newPileTradeProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();

        if (Objects.isNull(trade)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货订单不存在");
        }

        BaseResponse<FindPickTradeResponse> response =
                newPilePickTradeItemQueryProvider.findPickTradeList(FindPickTradeRequest.builder().pileTradeNo(trade.getId()).build());

        FindPickTradeResponse pickOrderResponse = response.getContext();
        return BaseResponse.success(pickOrderResponse);
    }

    /**
     * 查询订单付款记录
     */
    @ApiOperation(value = "查询订单付款记录")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/{tid}/payOrder", method = RequestMethod.GET)
    public BaseResponse<FindPayOrderResponse> payOrder(@PathVariable String tid) {
        NewPileTradeVO trade = newPileTradeProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        FindPayOrderResponse payOrderResponse = null;
        try {
            BaseResponse<FindPayOrderResponse> response = payOrderQueryProvider.findPayOrder(FindPayOrderRequest.builder().value(trade.getId()).build());

            payOrderResponse = response.getContext();

        } catch (SbcRuntimeException e) {
            if ("K-070001".equals(e.getErrorCode())) {
                payOrderResponse = new FindPayOrderResponse();
                payOrderResponse.setPayType(PayType.fromValue(Integer.valueOf(trade.getPayInfo().getPayTypeId())));
                payOrderResponse.setTotalPrice(trade.getTradePrice().getTotalPrice());
            }
        }
        return BaseResponse.success(payOrderResponse);
    }

    @ApiOperation(value = "查看订单详情-商家端")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/{tid}/detail", method = RequestMethod.GET)
    public BaseResponse<NewPileTradeVO> supplierDetail(@PathVariable String tid) {
        NewPileTradeVO trade = newPileTradeProvider.getByIdManager(TradeGetByIdManagerRequest.builder().tid(tid)
                .storeId(commonUtil.getStoreId()).requestFrom(AccountType.s2bSupplier)
                .build()).getContext().getTradeVO();
        return BaseResponse.success(trade);
    }

    /**
     * 取消订单
     */
    @ApiOperation(value = "取消订单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/cancel/{tid}", method = RequestMethod.GET)
    @LcnTransaction
    public BaseResponse cancel(@PathVariable(value = "tid") String tid) {
        Operator operator = commonUtil.getOperator();
        TradeCancelRequest tradeCancelRequest = TradeCancelRequest.builder()
                .tid(tid).operator(operator).build();

        newPileTradeProvider.cancel(tradeCancelRequest);

        //操作日志记录
        operateLogMQUtil.convertAndSend("囤货订单", "取消订单", "取消订单：订单tid" + tid);
        return BaseResponse.SUCCESSFUL();
    }
}
