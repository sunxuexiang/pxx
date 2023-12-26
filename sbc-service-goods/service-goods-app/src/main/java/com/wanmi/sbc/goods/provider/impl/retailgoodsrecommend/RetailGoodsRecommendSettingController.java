package com.wanmi.sbc.goods.provider.impl.retailgoodsrecommend;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.retailgoodsrecommend.RetailGoodsRecommendSettingProvider;
import com.wanmi.sbc.goods.api.request.retailgoodscommend.RetailGoodsRecommendBatchModifySortRequest;
import com.wanmi.sbc.goods.api.request.retailgoodscommend.RetailGoodsRecommendDelByIdRequest;
import com.wanmi.sbc.goods.api.request.retailgoodscommend.RetailGoodsRecommendSettingBatchAddRequest;
import com.wanmi.sbc.goods.retailgoodsrecommend.request.RetailGoodsCommendSortRequest;
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
public class RetailGoodsRecommendSettingController implements RetailGoodsRecommendSettingProvider {

    @Autowired
    private RetailGoodsRecommendSettingService retailGoodsRecommendSettingService;

    @Override
    public BaseResponse batchAdd(RetailGoodsRecommendSettingBatchAddRequest request) {
        retailGoodsRecommendSettingService.batchAdd(request.getGoodsInfoIds());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse batchModifySort(RetailGoodsRecommendBatchModifySortRequest request) {
        retailGoodsRecommendSettingService.dragSort(KsBeanUtil.convertList(request.getRetailGoodsRecommendSortDTOS(), RetailGoodsCommendSortRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse delById(RetailGoodsRecommendDelByIdRequest request) {
        retailGoodsRecommendSettingService.delById(request.getRecommendId());
        //刷新缓存
        retailGoodsRecommendSettingService.fillRedis();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse delByIds(RetailGoodsRecommendDelByIdRequest request) {
        retailGoodsRecommendSettingService.delByIds(request.getRecommendIdList());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse fillRedis() {
        retailGoodsRecommendSettingService.fillRedis();
        return BaseResponse.SUCCESSFUL();
    }

}
