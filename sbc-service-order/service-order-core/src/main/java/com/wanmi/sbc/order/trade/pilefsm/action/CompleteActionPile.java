package com.wanmi.sbc.order.trade.pilefsm.action;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.ResultCode;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.customer.api.provider.growthvalue.CustomerGrowthValueProvider;
import com.wanmi.sbc.customer.api.provider.points.CustomerPointsDetailSaveProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.growthvalue.CustomerGrowthValueAddRequest;
import com.wanmi.sbc.customer.api.request.points.CustomerPointsDetailAddRequest;
import com.wanmi.sbc.customer.api.request.store.StoreByIdRequest;
import com.wanmi.sbc.customer.api.response.store.StoreByIdResponse;
import com.wanmi.sbc.customer.bean.enums.GrowthValueServiceType;
import com.wanmi.sbc.customer.bean.enums.OperateType;
import com.wanmi.sbc.customer.bean.enums.PointsServiceType;
import com.wanmi.sbc.goods.api.provider.goodstobeevaluate.GoodsTobeEvaluateSaveProvider;
import com.wanmi.sbc.goods.api.provider.storetobeevaluate.StoreTobeEvaluateSaveProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsModifySalesNumRequest;
import com.wanmi.sbc.goods.api.request.goodstobeevaluate.GoodsTobeEvaluateAddRequest;
import com.wanmi.sbc.goods.api.request.storetobeevaluate.StoreTobeEvaluateAddRequest;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.enums.EvaluateStatus;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.common.GoodsSalesNumMq;
import com.wanmi.sbc.order.common.OrderCommonService;
import com.wanmi.sbc.order.growthvalue.model.root.OrderGrowthValueTemp;
import com.wanmi.sbc.order.growthvalue.service.OrderGrowthValueTempService;
import com.wanmi.sbc.order.trade.fsm.params.StateRequest;
import com.wanmi.sbc.order.trade.model.entity.TradeState;
import com.wanmi.sbc.order.trade.model.entity.value.Buyer;
import com.wanmi.sbc.order.trade.model.entity.value.Supplier;
import com.wanmi.sbc.order.trade.model.entity.value.TradeEventLog;
import com.wanmi.sbc.order.trade.model.root.PileTrade;
import com.wanmi.sbc.order.trade.pilefsm.PileTradeAction;
import com.wanmi.sbc.order.trade.pilefsm.PileTradeStateContext;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.bean.vo.ConfigVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Administrator on 2017/4/21.
 */
@Component
public class CompleteActionPile extends PileTradeAction {

    @Autowired
    private OrderCommonService orderCommonService;

    @Autowired
    private AuditQueryProvider auditQueryProvider;

    @Autowired
    private GoodsTobeEvaluateSaveProvider goodsTobeEvaluateSaveProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private GoodsSalesNumMq goodsSalesNumMq;

    @Autowired
    private StoreTobeEvaluateSaveProvider storeTobeEvaluateSaveProvider;

    @Autowired
    private CustomerGrowthValueProvider customerGrowthValueProvider;

    @Autowired
    private CustomerPointsDetailSaveProvider customerPointsDetailSaveProvider;

    @Autowired
    private OrderGrowthValueTempService orderGrowthValueTempService;

    /**
     * 已收货 并且 已支付 | 已作废
     *
     * @param trade
     * @param request
     * @param tsc
     */
    @Override
    @LcnTransaction
    @Transactional
    protected void evaluateInternal(PileTrade trade, StateRequest request, PileTradeStateContext tsc) {
        trade = (PileTrade) request.getData();

        TradeState tradeState = trade.getTradeState();

        if (!tradeState.getPayState().equals(PayState.PAID)) {
            throw new SbcRuntimeException("K-050103");
        }

        String detail = String.format("订单%s已操作完成", trade.getId());
        if (tradeState.getFlowState() == FlowState.VOID) {
            detail = String.format("作废退款单，订单%s状态扭转为已完成", trade.getId());
        } else {
            trade.getTradeState().setEndTime(LocalDateTime.now());
            //商品评价相关
            processGoodsEvaluate(trade);
        }
        if (trade.getDeliverWay().equals(DeliverWay.PICK_SELF)){
            tradeState.setDeliverStatus(DeliverStatus.SHIPPED);
            trade.getTradeItems().forEach(param->{
                param.setDeliveredNum(param.getNum());
            });
            trade.getGifts().forEach(param->{
                param.setDeliveredNum(param.getNum());
            });
        }
        tradeState.setFlowState(FlowState.COMPLETED);

        trade.appendTradeEventLog(TradeEventLog
                .builder()
                .operator(tsc.getOperator())
                .eventType(FlowState.COMPLETED.getDescription())
                .eventDetail(detail)
                .eventTime(LocalDateTime.now())
                .build());

        LocalDateTime finalTime = orderCommonService.queryReturnTime();
        //订单可入账时间（订单可退时间依据）--状态流转时修改trade信息
        trade.getTradeState().setFinalTime(finalTime);

        save(trade);
        super.operationLogMq.convertAndSend(tsc.getOperator(), FlowState.COMPLETED.getDescription(), detail);

        // 处理用户积分成长值
        addCustomerAttribute(trade, finalTime);
    }

    private void processGoodsEvaluate(PileTrade trade) {
        TradeState tradeState = trade.getTradeState();
        Supplier supplier = trade.getSupplier();
        String orderNo = trade.getId();
        Buyer buyer = trade.getBuyer();
        //店铺自动评价时间
        List<ConfigVO> configVOList = auditQueryProvider.listTradeConfig().getContext().getConfigVOList();
        StoreTobeEvaluateAddRequest storeTobeEvaluateAddRequest = new StoreTobeEvaluateAddRequest();
        configVOList.forEach(configVO -> {
            if (configVO.getConfigType().equals("order_setting_apply_refund")) {
                if (configVO.getStatus().equals(0)) {
                    storeTobeEvaluateAddRequest.setAutoStoreEvaluateDate(LocalDate.now().plusDays(15));
                } else {
                    storeTobeEvaluateAddRequest.setAutoStoreEvaluateDate(LocalDate.now()
                            .plusDays(Integer.valueOf(JSONObject.parseObject(configVO.getContext()).get("day").toString()))
                            .plusDays(15));
                }
            }
        });
        trade.getTradeItems().forEach(tradeItem -> {
            goodsTobeEvaluateSaveProvider.add(GoodsTobeEvaluateAddRequest.builder()
                    .storeId(tradeItem.getStoreId())
                    .storeName(supplier.getStoreName())
                    .goodsId(tradeItem.getSpuId())
                    .goodsImg(tradeItem.getPic())
                    .goodsInfoId(tradeItem.getSkuId())
                    .goodsInfoName(tradeItem.getSkuName())
                    .goodsSpecDetail(tradeItem.getSpecDetails())
                    .buyTime(tradeState.getCreateTime())
                    .orderNo(orderNo)
                    .customerId(buyer.getId())
                    .customerName(buyer.getName())
                    .customerAccount(buyer.getAccount())
                    .evaluateStatus(EvaluateStatus.NO_EVALUATE)
                    .evaluateImgStatus(EvaluateStatus.NO_EVALUATE)
                    .autoGoodsEvaluateDate(storeTobeEvaluateAddRequest.getAutoStoreEvaluateDate())
                    .createTime(LocalDateTime.now())
                    .createPerson(buyer.getName())
                    .build());
            //更新商品销量
            GoodsModifySalesNumRequest goodsModifySalesNumRequest = new GoodsModifySalesNumRequest();
            goodsModifySalesNumRequest.setGoodsId(tradeItem.getSpuId());
            goodsModifySalesNumRequest.setGoodsSalesNum(tradeItem.getNum());
            goodsSalesNumMq.updateGoodsSalesNumMq(goodsModifySalesNumRequest);
        });
        String storeLogo = "";
        BaseResponse<StoreByIdResponse> storeResponseBaseResponse = storeQueryProvider.getById(StoreByIdRequest.builder().
                storeId(supplier.getStoreId()).build());
        if (storeResponseBaseResponse.getCode().equals(ResultCode.SUCCESSFUL)) {
            StoreByIdResponse storeByIdResponse = storeResponseBaseResponse.getContext();
            storeLogo = storeByIdResponse.getStoreVO().getStoreLogo();
        }
        storeTobeEvaluateSaveProvider.add(StoreTobeEvaluateAddRequest.builder()
                .storeId(supplier.getStoreId())
                .storeLogo(storeLogo)
                .storeName(supplier.getStoreName())
                .orderNo(orderNo)
                .buyTime(tradeState.getCreateTime())
                //订单商品种类数量
                .goodsNum(trade.getTradeItems().size())
                .customerId(buyer.getId())
                .customerName(buyer.getName())
                .customerAccount(buyer.getAccount())
                .autoStoreEvaluateDate(storeTobeEvaluateAddRequest.getAutoStoreEvaluateDate())
                .createPerson(buyer.getName())
                .createTime(LocalDateTime.now())
                .build());

    }

    private void addCustomerAttribute(PileTrade trade, LocalDateTime finalTime) {
        // 保存定时任务增加用户成长值的记录
        OrderGrowthValueTemp tempRecord = new OrderGrowthValueTemp();
        tempRecord.setOrderNo(trade.getId());
        tempRecord.setReturnEndTime(finalTime);
        orderGrowthValueTempService.add(tempRecord);

        // 分享购买获得积分
        if (StringUtils.isNotBlank(trade.getShareUserId())) {
            customerGrowthValueProvider.increaseGrowthValue(
                    CustomerGrowthValueAddRequest.builder()
                            .customerId(trade.getShareUserId())
                            .type(OperateType.GROWTH)
                            .serviceType(GrowthValueServiceType.SHAREPURCHASE)
                            .build());
            customerPointsDetailSaveProvider.add(CustomerPointsDetailAddRequest.builder()
                    .customerId(trade.getShareUserId())
                    .type(OperateType.GROWTH)
                    .serviceType(PointsServiceType.SHAREPURCHASE)
                    .build());
        }
    }

}
