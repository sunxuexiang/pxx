package com.wanmi.sbc.wms.provider.impl.erp;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.wms.api.provider.erp.PushOrderKingdeeProvider;
import com.wanmi.sbc.wms.api.request.erp.DescriptionFailedQueryStockPushRequest;
import com.wanmi.sbc.wms.api.request.erp.PileTradePushRequest;
import com.wanmi.sbc.wms.api.request.erp.PileTradePushReturnGoodsRequest;
import com.wanmi.sbc.wms.api.response.erp.DescriptionFailedQueryStockPushResponse;
import com.wanmi.sbc.wms.api.response.erp.DescriptionFailedQueryStockPushReturnGoodsResponse;
import com.wanmi.sbc.wms.bean.vo.DescriptionFailedQueryStockPushReturnGoodsVO;
import com.wanmi.sbc.wms.bean.vo.DescriptionFailedQueryStockPushVO;
import com.wanmi.sbc.wms.erp.model.root.StockPushKingdeeOrder;
import com.wanmi.sbc.wms.erp.model.root.StockPushKingdeeReturnGoods;
import com.wanmi.sbc.wms.erp.service.CompensateStockpilingOrdersService;
import com.wanmi.sbc.wms.erp.service.PilePushErpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 推金蝶囤货订单
 *
 * @author yitang
 * @version 1.0
 */
@RestController
public class PushOrderKingdeeController implements PushOrderKingdeeProvider {

    @Autowired
    private PilePushErpService pilePushErpService;

    @Autowired
    private CompensateStockpilingOrdersService compensateStockpilingOrdersService;

    @Override
    public BaseResponse pushSalesKingdee(PileTradePushRequest tradePushRequest) {
        pilePushErpService.pushSalesKingdee(tradePushRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse pushReturnGoodsKingdee(PileTradePushReturnGoodsRequest pushReturnGoodsRequest) {
        pilePushErpService.pushReturnGoodsKingdee(pushReturnGoodsRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<DescriptionFailedQueryStockPushResponse> findStockPushKingdeeOrderOrders(@RequestBody @Valid DescriptionFailedQueryStockPushRequest stockPushRequest) {
        List<StockPushKingdeeOrder> tradePushKingdeeOrderList = compensateStockpilingOrdersService.findStockPushKingdeeOrderOrders(stockPushRequest.getPushKingdeeId(),stockPushRequest.getCreateTime());
        if (tradePushKingdeeOrderList.size() > 0){
            List<DescriptionFailedQueryStockPushVO> stockPushVOList = KsBeanUtil.convertList(tradePushKingdeeOrderList, DescriptionFailedQueryStockPushVO.class);
            DescriptionFailedQueryStockPushResponse pushResponse = DescriptionFailedQueryStockPushResponse.builder()
                                                                                                          .stockPushVO(stockPushVOList)
                                                                                                          .build();
            return BaseResponse.success(pushResponse);
        }
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<DescriptionFailedQueryStockPushReturnGoodsResponse> findStockPushKingdeeReturnGoodsOrders(@RequestBody @Valid DescriptionFailedQueryStockPushRequest stockPushRequest) {
        List<StockPushKingdeeReturnGoods> returnGoodsList = compensateStockpilingOrdersService.findStockPushKingdeeReturnGoodsOrder(stockPushRequest.getPushKingdeeId(), stockPushRequest.getCreateTime());
        if (returnGoodsList.size() > 0){
            List<DescriptionFailedQueryStockPushReturnGoodsVO> stockPushReturnGoodsVOList = KsBeanUtil.convertList(returnGoodsList,DescriptionFailedQueryStockPushReturnGoodsVO.class);
            DescriptionFailedQueryStockPushReturnGoodsResponse goodsResponse = DescriptionFailedQueryStockPushReturnGoodsResponse.builder()
                                                                                                                                 .returnGoodsVOList(stockPushReturnGoodsVOList)
                                                                                                                                 .build();
            return BaseResponse.success(goodsResponse);
        }
        return BaseResponse.SUCCESSFUL();
    }

}
