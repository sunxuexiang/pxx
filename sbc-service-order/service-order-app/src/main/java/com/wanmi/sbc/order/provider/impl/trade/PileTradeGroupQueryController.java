package com.wanmi.sbc.order.provider.impl.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.order.api.provider.trade.PileTradeGroupQueryProvider;
import com.wanmi.sbc.order.api.request.trade.TradeGroupByIdsRequest;
import com.wanmi.sbc.order.api.response.trade.TradeGroupByGroupIdsResponse;
import com.wanmi.sbc.order.bean.vo.TradeGroupVO;
import com.wanmi.sbc.order.trade.model.root.PileTradeGroup;
import com.wanmi.sbc.order.trade.service.PileTradeGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
public class PileTradeGroupQueryController implements PileTradeGroupQueryProvider {

    @Autowired
    private PileTradeGroupService pileTradeGroupService;

    /**
     * @param tradeGroupByIdsRequest 参数 {@link TradeGroupByIdsRequest}
     * @return
     */
    @Override
    public BaseResponse<TradeGroupByGroupIdsResponse> getTradeGroupByGroupIds(TradeGroupByIdsRequest tradeGroupByIdsRequest) {

        if(CollectionUtils.isEmpty(tradeGroupByIdsRequest.getGroupId()) || tradeGroupByIdsRequest.getGroupId().size() < 1){
            return BaseResponse.success(TradeGroupByGroupIdsResponse.builder().build());
        }

        List<PileTradeGroup> tradeGroups = pileTradeGroupService.findByIds(tradeGroupByIdsRequest.getGroupId());
        if(CollectionUtils.isEmpty(tradeGroups) || tradeGroups.size() < 1){
            return BaseResponse.success(TradeGroupByGroupIdsResponse.builder().build());
        }
        return BaseResponse.success(TradeGroupByGroupIdsResponse.builder().tradeGroupVOS(KsBeanUtil.convertList(tradeGroups, TradeGroupVO.class)).build());
    }
}
