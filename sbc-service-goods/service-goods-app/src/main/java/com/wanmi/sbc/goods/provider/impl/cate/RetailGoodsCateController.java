package com.wanmi.sbc.goods.provider.impl.cate;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.cate.RetailGoodsCateProvider;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateAddRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateBatchModifySortRequest;
import com.wanmi.sbc.goods.cate.request.GoodsCateSortRequest;
import com.wanmi.sbc.goods.cate.service.RetailGoodsCateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 散批商品分类接口实现类
 * @author: XinJiang
 * @time: 2022/5/5 15:53
 */
@RestController
@Validated
public class RetailGoodsCateController implements RetailGoodsCateProvider {

    @Autowired
    private RetailGoodsCateService retailGoodsCateService;

    @Override
    public BaseResponse addRetailGoodsCate(GoodsCateAddRequest request) {
        retailGoodsCateService.addRetailGoodsCate(request.getCateIds(), request.getCateGrade());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse delRetailGoodsCate(GoodsCateAddRequest request) {
        retailGoodsCateService.delRetailGoodsCateRecommend(request.getCateIds(), request.getCateGrade());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse batchRetailGoodsCateModifySort(GoodsCateBatchModifySortRequest request) {
        retailGoodsCateService.retailGoodsCateDragSort(KsBeanUtil.convert(request.getGoodsCateSortDTOList()
                , GoodsCateSortRequest.class), request.getCateGrade());
        return BaseResponse.SUCCESSFUL();
    }
}
