package com.wanmi.sbc.returnorder.provider.impl.trade.newPileTrade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.api.provider.trade.newPileTrade.NewPileTradeItemQueryProvider;
import com.wanmi.sbc.returnorder.api.request.trade.TradeItemByCustomerIdRequest;
import com.wanmi.sbc.returnorder.api.request.trade.TradeItemSnapshotRequest;
import com.wanmi.sbc.returnorder.api.request.trade.TradeItemStockOutRequest;
import com.wanmi.sbc.returnorder.api.response.trade.TradeItemByCustomerIdResponse;
import com.wanmi.sbc.returnorder.api.response.trade.TradeItemStockOutResponse;
import com.wanmi.sbc.returnorder.bean.vo.TradeItemGroupVO;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeItem;
import com.wanmi.sbc.returnorder.trade.model.root.TradeItemGroup;
import com.wanmi.sbc.returnorder.trade.service.newPileTrade.NewPileTradeItemService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>订单商品服务查询接口</p>
 * @Author: daiyitian
 * @Description: 退单服务查询接口
 * @Date: 2018-12-03 15:40
 */
@Validated
@RestController
public class NewPileTradeItemQueryController implements NewPileTradeItemQueryProvider {

    @Autowired
    private NewPileTradeItemService newPileTradeItemService;

    /**
     * 根据客户id查询已确认订单商品快照
     *
     * @param request 根据客户id查询已确认订单商品快照请求结构 {@link TradeItemByCustomerIdRequest}
     * @return 订单商品快照列表 {@link TradeItemByCustomerIdResponse}
     */
    @Override
    public BaseResponse<TradeItemByCustomerIdResponse> listByCustomerId(@Valid TradeItemByCustomerIdRequest
                                                                         request) {
        List<TradeItemGroup> groupList = newPileTradeItemService.find(request.getCustomerId());
        List<TradeItemGroupVO> groupVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(groupList)) {
            groupVOList = KsBeanUtil.convert(groupList, TradeItemGroupVO.class);
        }
        return BaseResponse.success(TradeItemByCustomerIdResponse.builder().tradeItemGroupList(groupVOList).build());
    }

    @Override
    public BaseResponse<TradeItemByCustomerIdResponse> listAllByCustomerId(@Valid TradeItemByCustomerIdRequest request) {
        List<TradeItemGroup> groupList = newPileTradeItemService.findAll(request.getCustomerId());
        List<TradeItemGroupVO> groupVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(groupList)) {
            groupVOList = KsBeanUtil.convert(groupList, TradeItemGroupVO.class);
        }
        return BaseResponse.success(TradeItemByCustomerIdResponse.builder().tradeItemGroupList(groupVOList).build());
    }

    @Override
    public BaseResponse<TradeItemStockOutResponse> listStockOutGroupByStoreId( @Valid TradeItemStockOutRequest request) {
        return BaseResponse.success(TradeItemStockOutResponse.builder()
                .stockOutGroupByStoreId( newPileTradeItemService.queryStockNum(request.getCustomerId()
                        , request.getWareId(),request.getStoreId())).build());
    }

    @Override
    public BaseResponse<TradeItemByCustomerIdResponse> itemListByCustomerId(@Valid TradeItemByCustomerIdRequest request) {
        List<TradeItemGroup> groupList = newPileTradeItemService.findItemByCustomerId(request.getCustomerId());
        List<TradeItemGroupVO> groupVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(groupList)) {
            groupVOList = KsBeanUtil.convert(groupList, TradeItemGroupVO.class);
        }
        return BaseResponse.success(TradeItemByCustomerIdResponse.builder().tradeItemGroupList(groupVOList).build());
    }

    @Override
    public BaseResponse snapshotRetail(@Valid TradeItemSnapshotRequest request) {
        newPileTradeItemService.snapshotRetail(request, KsBeanUtil.convert(request.getTradeItems(), TradeItem.class),
                request.getTradeMarketingList(), request.getSkuList());
        return BaseResponse.SUCCESSFUL();
    }
}
