package com.wanmi.sbc.order.returnorder.pilefsm.action;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.customer.api.provider.points.CustomerPointsDetailSaveProvider;
import com.wanmi.sbc.customer.api.request.points.CustomerPointsDetailAddRequest;
import com.wanmi.sbc.customer.bean.enums.OperateType;
import com.wanmi.sbc.customer.bean.enums.PointsServiceType;
import com.wanmi.sbc.goods.api.provider.goodstobeevaluate.GoodsTobeEvaluateQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodstobeevaluate.GoodsTobeEvaluateSaveProvider;
import com.wanmi.sbc.goods.api.provider.storetobeevaluate.StoreTobeEvaluateQueryProvider;
import com.wanmi.sbc.goods.api.provider.storetobeevaluate.StoreTobeEvaluateSaveProvider;
import com.wanmi.sbc.goods.api.request.goodstobeevaluate.GoodsTobeEvaluateDelByIdListRequest;
import com.wanmi.sbc.goods.api.request.goodstobeevaluate.GoodsTobeEvaluateListRequest;
import com.wanmi.sbc.goods.api.request.storetobeevaluate.StoreTobeEvaluateDelByIdListRequest;
import com.wanmi.sbc.goods.api.request.storetobeevaluate.StoreTobeEvaluateListRequest;
import com.wanmi.sbc.goods.bean.vo.GoodsTobeEvaluateVO;
import com.wanmi.sbc.goods.bean.vo.StoreTobeEvaluateVO;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.returnorder.pilefsm.ReturnPileAction;
import com.wanmi.sbc.order.returnorder.fsm.ReturnStateContext;
import com.wanmi.sbc.order.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.order.returnorder.fsm.params.ReturnStateRequest;
import com.wanmi.sbc.order.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.order.returnorder.model.value.ReturnEventLog;
import com.wanmi.sbc.order.returnorder.pilefsm.ReturnPileStateContext;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 退款操作
 * Created by jinwei on 22/4/2017.
 */
@Component
public class RefundReturnPileAction extends ReturnPileAction {
    @Autowired
    private CustomerPointsDetailSaveProvider customerPointsDetailSaveProvider;

    @Autowired
    private GoodsTobeEvaluateSaveProvider goodsTobeEvaluateSaveProvider;

    @Autowired
    private GoodsTobeEvaluateQueryProvider goodsTobeEvaluateQueryProvider;

    @Autowired
    private StoreTobeEvaluateSaveProvider storeTobeEvaluateSaveProvider;

    @Autowired
    private StoreTobeEvaluateQueryProvider storeTobeEvaluateQueryProvider;

    @Override
    protected void evaluateInternal(ReturnPileOrder returnPileOrder, ReturnStateRequest request, ReturnPileStateContext rsc) {
        Operator operator = rsc.findOperator();
        returnPileOrder.setReturnFlowState(ReturnFlowState.COMPLETED);
        returnPileOrder.getReturnPrice().setActualReturnPrice(rsc.findRequestData());
        ReturnEventLog eventLog = ReturnEventLog.builder()
                .operator(operator)
                .eventType(ReturnEvent.REFUND.getDesc())
                .eventTime(LocalDateTime.now())
                .eventDetail(String.format("退单[%s]已退款，退单完成,操作人:%s", returnPileOrder.getId(), operator.getName()))
                .build();
        returnPileOrder.appendReturnEventLog(eventLog);
        returnPileOrder.setFinishTime(LocalDateTime.now());
        returnPileOrderService.updateReturnOrder(returnPileOrder);
        super.operationLogMq.convertAndSend(operator, ReturnEvent.REFUND.getDesc(), eventLog.getEventDetail());

        Long points = Objects.nonNull(returnPileOrder.getReturnPoints()) ? returnPileOrder.getReturnPoints().getApplyPoints() : null;
        if (points != null && points > 0) {
            customerPointsDetailSaveProvider.returnPoints(CustomerPointsDetailAddRequest.builder()
                    .customerId(returnPileOrder.getBuyer().getId())
                    .type(OperateType.GROWTH)
                    .serviceType(PointsServiceType.RETURN_ORDER_BACK)
                    .points(points)
                    .content(JSONObject.toJSONString(Collections.singletonMap("returnOrderNo", returnPileOrder.getId())))
                    .build());
        }
        delEvaluate(returnPileOrder);
    }

    /**
     * @Author lvzhenwei
     * @Description 订单退货完成删除对应的订单以及商品待评价数据
     * @Date 11:28 2019/4/11
     * @Param [returnOrder]
     **/
    private void delEvaluate(ReturnPileOrder returnPileOrder) {
        String tid = returnPileOrder.getTid();
        returnPileOrder.getReturnItems().forEach(returnItem -> {
            //判断退单商品是否全部退完，如果已经退完，则删除对应的商品待评论数据
            List<String> goodsTobeEvaluateIds;
            if (returnItem.getCanReturnNum() == returnItem.getNum()) {
                GoodsTobeEvaluateListRequest goodsTobeEvaluateListReq = new GoodsTobeEvaluateListRequest();
                goodsTobeEvaluateListReq.setOrderNo(tid);
                goodsTobeEvaluateListReq.setGoodsInfoId(returnItem.getSkuId());
                goodsTobeEvaluateIds = goodsTobeEvaluateQueryProvider.list(goodsTobeEvaluateListReq).getContext().getGoodsTobeEvaluateVOList()
                        .stream().map(GoodsTobeEvaluateVO::getId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(goodsTobeEvaluateIds)) {
                    GoodsTobeEvaluateDelByIdListRequest goodsTobeEvaluateDelByIdListRequest = new GoodsTobeEvaluateDelByIdListRequest();
                    goodsTobeEvaluateDelByIdListRequest.setIdList(goodsTobeEvaluateIds);
                    goodsTobeEvaluateSaveProvider.deleteByIdList(goodsTobeEvaluateDelByIdListRequest);
                }
            }
        });
        //如果订单全部退完，则删除对应订单店铺服务待评价数据
        if (returnPileOrderService.isReturnFull(returnPileOrder)) {
            StoreTobeEvaluateListRequest storeTobeEvaluateListReq = new StoreTobeEvaluateListRequest();
            storeTobeEvaluateListReq.setOrderNo(tid);
            List<String> storeTobeEvaluateIds;
            storeTobeEvaluateIds = storeTobeEvaluateQueryProvider.list(storeTobeEvaluateListReq).getContext().getStoreTobeEvaluateVOList()
                    .stream().map(StoreTobeEvaluateVO::getId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(storeTobeEvaluateIds)) {
                StoreTobeEvaluateDelByIdListRequest storeTobeEvaluateDelByIdListRequest = new StoreTobeEvaluateDelByIdListRequest();
                storeTobeEvaluateDelByIdListRequest.setIdList(storeTobeEvaluateIds);
                storeTobeEvaluateSaveProvider.deleteByIdList(storeTobeEvaluateDelByIdListRequest);
            }
        }
    }
}
