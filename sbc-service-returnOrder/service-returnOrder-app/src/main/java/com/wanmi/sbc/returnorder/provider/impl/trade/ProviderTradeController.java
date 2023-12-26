package com.wanmi.sbc.returnorder.provider.impl.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.api.provider.trade.ProviderTradeProvider;
import com.wanmi.sbc.returnorder.api.request.trade.*;
import com.wanmi.sbc.returnorder.api.response.trade.FindProviderTradeResponse;
import com.wanmi.sbc.returnorder.api.response.trade.TradeDeliverResponse;
import com.wanmi.sbc.returnorder.api.response.trade.TradeProviderResponse;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeDeliver;
import com.wanmi.sbc.returnorder.trade.model.root.ProviderTrade;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.service.ProviderTradeService;
import com.wanmi.sbc.returnorder.trade.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: 供应商订单处理
 * @Autho qiaokang
 * @Date：2020-03-27 09:17
 */
@Validated
@RestController
public class ProviderTradeController implements ProviderTradeProvider {

    @Autowired
    private ProviderTradeService providerTradeService;

    @Autowired
    private TradeService tradeService;

    private AtomicInteger exportCount = new AtomicInteger(0);

    /**
     * 更新订单
     *
     * @param tradeUpdateRequest 订单信息 {@link TradeUpdateRequest}
     * @return
     */
    @Override
    public BaseResponse providerUpdate(@RequestBody @Valid TradeUpdateRequest tradeUpdateRequest) {
        providerTradeService.updateProviderTrade(tradeUpdateRequest);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 发货校验,检查请求发货商品数量是否符合应发货数量
     *
     * @param tradeDeliveryCheckRequest 订单号 物流信息 {@link TradeDeliveryCheckRequest}
     * @return
     */
    @Override
    public BaseResponse providerDeliveryCheck(@RequestBody @Valid TradeDeliveryCheckRequest tradeDeliveryCheckRequest) {
        providerTradeService.deliveryCheck(tradeDeliveryCheckRequest.getTid(),
                KsBeanUtil.convert(tradeDeliveryCheckRequest.getTradeDeliver(), com.wanmi.sbc.returnorder.trade.request.TradeDeliverRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 发货
     *
     * @param tradeDeliverRequest 物流信息 操作信息 {@link TradeDeliverRequest}
     * @return
     */
    @Override
    public BaseResponse<TradeDeliverResponse> providerDeliver(@RequestBody @Valid com.wanmi.sbc.returnorder.api.request.trade.TradeDeliverRequest tradeDeliverRequest) {
        String deliverId = providerTradeService.deliver(tradeDeliverRequest.getTid(),
                KsBeanUtil.convert(tradeDeliverRequest.getTradeDeliver(), TradeDeliver.class),
                tradeDeliverRequest.getOperator());
        return BaseResponse.success(TradeDeliverResponse.builder().deliverId(deliverId).build());
    }

	/**
     * 根据主订单ID查询子订单
     */
    @Override
    public BaseResponse<FindProviderTradeResponse> findByParentIdList(@RequestBody @Valid FindProviderTradeRequest findProviderTradeRequest){
        List<TradeVO> tradeVOList=KsBeanUtil.convert(providerTradeService.findListByParentIdList(findProviderTradeRequest.getParentId()), TradeVO.class);
        return BaseResponse.success(FindProviderTradeResponse.builder().tradeVOList(tradeVOList).build());
    }

    /**
     * 修改卖家备注
     *
     * @param providerTradeRemedyBuyerRemarkRequest 订单修改相关必要信息 {@link ProviderTradeRemedySellerRemarkRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse remedyBuyerRemark(@RequestBody @Valid ProviderTradeRemedyBuyerRemarkRequest providerTradeRemedyBuyerRemarkRequest) {
        providerTradeService.remedyBuyerRemark(providerTradeRemedyBuyerRemarkRequest.getTid(),
                providerTradeRemedyBuyerRemarkRequest.getBuyRemark(),
                providerTradeRemedyBuyerRemarkRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }



    @Override
    public BaseResponse<TradeProviderResponse> providerByidAndPid(@RequestBody @Valid TradeGetByIdAndPidRequest tradeGetByIdAndPidRequest) {
        String providerTradeId =  providerTradeService.providerByidAndPid(tradeGetByIdAndPidRequest.getTid(),tradeGetByIdAndPidRequest.getProviderId());
        return BaseResponse.success(TradeProviderResponse.builder().providerTradeId(providerTradeId).build());
    }

    /**
     * 发货记录作废
     *
     * @param tradeDeliverRecordObsoleteRequest 订单编号 物流单号 操作信息 {@link TradeDeliverRecordObsoleteRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    @Transactional
    public BaseResponse deliverRecordObsolete(@RequestBody @Valid TradeDeliverRecordObsoleteRequest tradeDeliverRecordObsoleteRequest) {
        ProviderTrade providerTrade = providerTradeService.providerDetail(tradeDeliverRecordObsoleteRequest.getTid());
        Trade trade = tradeService.detail(providerTrade.getParentId());
        List<TradeDeliver> parentTradeDelivers = trade.getTradeDelivers();
        for (TradeDeliver deliver : parentTradeDelivers) {
            if(tradeDeliverRecordObsoleteRequest.getDeliverId().equals(deliver.getSunDeliverId())){
                tradeService.deliverRecordObsolete(trade.getId(),
                        deliver.getDeliverId(),
                        tradeDeliverRecordObsoleteRequest.getOperator());
            }
        }
        return BaseResponse.SUCCESSFUL();
    }


}
