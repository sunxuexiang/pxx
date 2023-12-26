package com.wanmi.sbc.returnorder.provider.impl.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.api.provider.trade.PileTradeItemQueryProvider;
import com.wanmi.sbc.returnorder.api.request.trade.TradeItemByCustomerIdRequest;
import com.wanmi.sbc.returnorder.api.request.trade.TradeItemStockOutRequest;
import com.wanmi.sbc.returnorder.api.response.trade.TradeItemByCustomerIdResponse;
import com.wanmi.sbc.returnorder.api.response.trade.TradeItemStockOutResponse;
import com.wanmi.sbc.returnorder.bean.vo.TradeItemGroupVO;
import com.wanmi.sbc.returnorder.trade.model.root.TradeItemGroup;
import com.wanmi.sbc.returnorder.trade.service.PileTradeItemService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 囤货订单商品服务查询接口
 * @author: jiangxin
 * @create: 2021-09-30 16:03
 */
@Validated
@RestController
public class PileTradeItemQueryController implements PileTradeItemQueryProvider {

    @Autowired
    private PileTradeItemService tradeItemService;

    /**
     * 根据客户id查询已确认订单商品快照
     *
     * @param request 根据客户id查询已确认订单商品快照请求结构 {@link TradeItemByCustomerIdRequest}
     * @return 订单商品快照列表 {@link TradeItemByCustomerIdResponse}
     */
    @Override
    public BaseResponse<TradeItemByCustomerIdResponse> listByCustomerId(@RequestBody @Valid TradeItemByCustomerIdRequest
                                                                                request) {
        List<TradeItemGroup> groupList = tradeItemService.find(request.getCustomerId());
        List<TradeItemGroupVO> groupVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(groupList)) {
            groupVOList = KsBeanUtil.convert(groupList, TradeItemGroupVO.class);
        }
        return BaseResponse.success(TradeItemByCustomerIdResponse.builder().tradeItemGroupList(groupVOList).build());
    }

    @Override
    public BaseResponse<TradeItemStockOutResponse> listStockOutGroupByStoreId(@RequestBody  @Valid TradeItemStockOutRequest request) {
        return BaseResponse.success(TradeItemStockOutResponse.builder()
                .stockOutGroupByStoreId( tradeItemService.queryStockNum(request.getCustomerId()
                        , request.getWareId(),request.getStoreId())).build());
    }

}
