package com.wanmi.sbc.goods.provider.impl.goods;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StoreQueryRequest;
import com.wanmi.sbc.customer.api.response.store.StoreSimpleResponse;
import com.wanmi.sbc.goods.api.provider.goods.GoodsHandlerProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsCopyByStoreRequest;
import com.wanmi.sbc.goods.info.service.GoodsHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-12-02 14:20
 **/
@RestController
@Slf4j
public class GoodsHandlerController implements GoodsHandlerProvider {


    @Autowired
    private GoodsHandlerService goodsHandlerService;

    @Autowired
    private StoreQueryProvider storeQueryProvider;


    @Override
    public BaseResponse<List<String>> copyGoodsByStore(GoodsCopyByStoreRequest request) {
        request.checkParams();
        final StoreQueryRequest storeQueryRequest = new StoreQueryRequest();
        storeQueryRequest.setStoreIds(Lists.newArrayList(request.getStoreId()));
        final BaseResponse<List<StoreSimpleResponse>> listBaseResponse = storeQueryProvider.listSimple(storeQueryRequest);
        final StoreSimpleResponse storeSimpleResponse = listBaseResponse.getContext().get(0);
        return BaseResponse.success(goodsHandlerService.copyGoodsByStore(request, storeSimpleResponse));
    }
}
