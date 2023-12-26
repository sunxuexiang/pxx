package com.wanmi.sbc.goods.provider.impl.retailgoodsrecommend;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.retailgoodsrecommend.BulkGoodsRecommendSettingProvider;
import com.wanmi.sbc.goods.api.provider.retailgoodsrecommend.RetailGoodsRecommendSettingProvider;
import com.wanmi.sbc.goods.api.request.retailgoodscommend.*;
import com.wanmi.sbc.goods.retailgoodsrecommend.request.BulkGoodsCommendSortRequest;
import com.wanmi.sbc.goods.retailgoodsrecommend.request.RetailGoodsCommendSortRequest;
import com.wanmi.sbc.goods.retailgoodsrecommend.service.BulkGoodsRecommendSettingService;
import com.wanmi.sbc.goods.retailgoodsrecommend.service.RetailGoodsRecommendSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 散批惊喜推荐商品服务接口实现类
 * @author: XinJiang
 * @time: 2022/4/20 10:10
 */
@RestController
@Validated
public class BulkGoodsRecommendSettingController implements BulkGoodsRecommendSettingProvider {

    @Autowired
    private BulkGoodsRecommendSettingService bulkGoodsRecommendSettingService;

    @Override
    public BaseResponse batchAdd(BulkGoodsRecommendSettingBatchAddRequest request) {
        bulkGoodsRecommendSettingService.batchAdd(request.getGoodsInfoIds(),request.getWareId());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse batchModifySort(BulkGoodsRecommendBatchModifySortRequest request) {
        bulkGoodsRecommendSettingService.dragSort(KsBeanUtil.convertList(request.getRetailGoodsRecommendSortDTOS(), BulkGoodsCommendSortRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse delById(BulkGoodsRecommendDelByIdRequest request) {
        bulkGoodsRecommendSettingService.delById(request.getRecommendId());
        //刷新缓存
        bulkGoodsRecommendSettingService.fillRedis();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse delByIds(BulkGoodsRecommendDelByIdRequest request) {
        bulkGoodsRecommendSettingService.delByIds(request.getRecommendIdList());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse fillRedis() {
        bulkGoodsRecommendSettingService.fillRedis();
        return BaseResponse.SUCCESSFUL();
    }

}
