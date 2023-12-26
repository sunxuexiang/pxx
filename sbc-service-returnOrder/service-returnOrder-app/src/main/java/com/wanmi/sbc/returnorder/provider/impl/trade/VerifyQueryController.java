package com.wanmi.sbc.returnorder.provider.impl.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.api.provider.trade.VerifyQueryProvider;
import com.wanmi.sbc.returnorder.bean.vo.TradeGoodsListVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeItemVO;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeItem;
import com.wanmi.sbc.returnorder.trade.service.VerifyService;
import com.wanmi.sbc.returnorder.api.request.trade.*;
import com.wanmi.sbc.returnorder.api.response.trade.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-12-04
 */
@Slf4j
@Validated
@RestController
public class VerifyQueryController implements VerifyQueryProvider {

    @Autowired
    private VerifyService verifyService;

    /**
     * @param verifyGoodsRequest {@link VerifyGoodsRequest} 包含以下参数：
     *                           tradeItems        订单商品数据，仅包含skuId与购买数量
     *                           oldTradeItems     旧订单商品数据，可以为emptyList，用于编辑订单的场景，由于旧订单商品库存已先还回但事务未提交，因此在处理中将库存做逻辑叠加
     *                           goodsInfoResponse 关联商品信息
     *                           storeId           店铺ID
     *                           isFull            是否填充订单商品信息与设价(区间价/已经算好的会员价)
     * @return
     */
    @Override
    public BaseResponse<VerifyGoodsResponse> verifyGoods(@RequestBody @Valid VerifyGoodsRequest verifyGoodsRequest) {
        List<TradeItem> tradeItems = verifyService.verifyGoods(KsBeanUtil.convertList(verifyGoodsRequest.getTradeItems(), TradeItem.class),
                KsBeanUtil.convertList(verifyGoodsRequest.getOldTradeItems(), TradeItem.class),
                KsBeanUtil.convert(verifyGoodsRequest.getGoodsInfoResponse(), TradeGoodsListVO.class),
                verifyGoodsRequest.getStoreId(), verifyGoodsRequest.getIsFull(),verifyGoodsRequest.getCheckStockFlag());
        VerifyGoodsResponse verifyGoodsResponse = new VerifyGoodsResponse();
        verifyGoodsResponse.setTradeItems(KsBeanUtil.convertList(tradeItems, TradeItemVO.class));
        return BaseResponse.success(verifyGoodsResponse);
    }



    /**
     * @param verifyGoodsRequest {@link VerifyGoodsRequest} 包含以下参数：
     *                           tradeItems        订单商品数据，仅包含skuId与购买数量
     *                           oldTradeItems     旧订单商品数据，可以为emptyList，用于编辑订单的场景，由于旧订单商品库存已先还回但事务未提交，因此在处理中将库存做逻辑叠加
     *                           goodsInfoResponse 关联商品信息
     *                           storeId           店铺ID
     *                           isFull            是否填充订单商品信息与设价(区间价/已经算好的会员价)
     * @return
     */
    @Override
    public BaseResponse<VerifyGoodsResponse> verifyGoodsDevanning(@RequestBody @Valid VerifyGoodsRequest verifyGoodsRequest) {
        List<TradeItem> tradeItems = verifyService.verifyGoodsDevanning(KsBeanUtil.convertList(verifyGoodsRequest.getTradeItems(), TradeItem.class),
                KsBeanUtil.convertList(verifyGoodsRequest.getOldTradeItems(), TradeItem.class),
                KsBeanUtil.convert(verifyGoodsRequest.getGoodsInfoResponse(), TradeGoodsListVO.class),
                verifyGoodsRequest.getStoreId(), verifyGoodsRequest.getIsFull(),verifyGoodsRequest.getCheckStockFlag());
        VerifyGoodsResponse verifyGoodsResponse = new VerifyGoodsResponse();
        verifyGoodsResponse.setTradeItems(KsBeanUtil.convertList(tradeItems, TradeItemVO.class));
        return BaseResponse.success(verifyGoodsResponse);
    }



    @Override
    public BaseResponse<VerifyGoodsResponse> verifyTakeGoods(@RequestBody @Valid VerifyGoodsRequest verifyGoodsRequest) {
        List<TradeItem> tradeItems = verifyService.verifyTakeGoods(KsBeanUtil.convertList(verifyGoodsRequest.getTradeItems(), TradeItem.class),
                KsBeanUtil.convertList(verifyGoodsRequest.getOldTradeItems(), TradeItem.class),
                KsBeanUtil.convert(verifyGoodsRequest.getGoodsInfoResponse(), TradeGoodsListVO.class),
                verifyGoodsRequest.getStoreId(), verifyGoodsRequest.getIsFull(),verifyGoodsRequest.getCheckStockFlag(),
                verifyGoodsRequest.getCustomerId());
        VerifyGoodsResponse verifyGoodsResponse = new VerifyGoodsResponse();
        verifyGoodsResponse.setTradeItems(KsBeanUtil.convertList(tradeItems, TradeItemVO.class));
        return BaseResponse.success(verifyGoodsResponse);
    }

    @Override
    public BaseResponse<VerifyPointsGoodsResponse> verifyPointsGoods(@RequestBody @Valid VerifyPointsGoodsRequest verifyPointsGoodsRequest) {
        TradeItem tradeItem = verifyService.verifyPointsGoods(KsBeanUtil.convert(verifyPointsGoodsRequest.getTradeItem(), TradeItem.class),
                KsBeanUtil.convert(verifyPointsGoodsRequest.getGoodsInfoResponse(), TradeGoodsListVO.class),
                verifyPointsGoodsRequest.getPointsGoodsVO(), verifyPointsGoodsRequest.getStoreId());
        VerifyPointsGoodsResponse verifyPointsGoodsResponse = new VerifyPointsGoodsResponse();
        verifyPointsGoodsResponse.setTradeItem(KsBeanUtil.convert(tradeItem, TradeItemVO.class));
        return BaseResponse.success(verifyPointsGoodsResponse);
    }

    /**
     * @param mergeGoodsInfoRequest {@link MergeGoodsInfoRequest}  包含以下参数：
     *                              tradeItems        订单商品数据，仅包含skuId/价格
     *                              goodsInfoResponse 关联商品信息
     * @return
     */
    @Override
    public BaseResponse<MergeGoodsInfoResponse> mergeGoodsInfo(@RequestBody @Valid MergeGoodsInfoRequest mergeGoodsInfoRequest) {
        List<TradeItem> tradeItems = verifyService.mergeGoodsInfo(KsBeanUtil.convertList(mergeGoodsInfoRequest.getTradeItems(), TradeItem.class),
                KsBeanUtil.convert(mergeGoodsInfoRequest.getGoodsInfoResponse(), TradeGetGoodsResponse.class));
        MergeGoodsInfoResponse mergeGoodsInfoResponse = new MergeGoodsInfoResponse();
        mergeGoodsInfoResponse.setTradeItems(KsBeanUtil.convertList(tradeItems, TradeItemVO.class));
        return BaseResponse.success(mergeGoodsInfoResponse);
    }

    /**
     * @param verifyStoreRequest {@link VerifyStoreRequest} 包含多个店铺ID集合
     * @return
     */
    @Override
    public BaseResponse verifyStore(@RequestBody @Valid VerifyStoreRequest verifyStoreRequest) {
        verifyService.verifyStore(verifyStoreRequest.getStoreIds());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param verifyTradeMarketingRequest {@link VerifyTradeMarketingRequest}
     * @return
     */
    @Override
    public BaseResponse<VerifyTradeMarketingResponse> verifyTradeMarketing(@RequestBody @Valid VerifyTradeMarketingRequest verifyTradeMarketingRequest) {
        verifyService.verifyTradeMargeting(verifyTradeMarketingRequest.getTradeMarketingList(),
                KsBeanUtil.convertList(verifyTradeMarketingRequest.getOldGifts(), TradeItem.class),
                KsBeanUtil.convertList(verifyTradeMarketingRequest.getTradeItems(), TradeItem.class),
                verifyTradeMarketingRequest.getCustomerId(),
                verifyTradeMarketingRequest.getIsFoceCommit(),
                verifyTradeMarketingRequest.getWareId());
        return BaseResponse.success(new VerifyTradeMarketingResponse(verifyTradeMarketingRequest.getTradeMarketingList()));
    }
}
