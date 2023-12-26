package com.wanmi.sbc.goods.provider.impl.cate;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.cate.RetailGoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateListByConditionRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateListByConditionResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.goods.cate.request.GoodsCateQueryRequest;
import com.wanmi.sbc.goods.cate.service.RetailGoodsCateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 散批商品分类查询实现类
 * @author: XinJiang
 * @time: 2022/5/5 15:59
 */
@RestController
@Validated
public class RetailGoodsCateQueryController implements RetailGoodsCateQueryProvider {

    @Autowired
    private RetailGoodsCateService retailGoodsCateService;

    @Override
    public BaseResponse<GoodsCateListByConditionResponse> listByCondition(GoodsCateListByConditionRequest request) {
        GoodsCateQueryRequest queryRequest = KsBeanUtil.convert(request,GoodsCateQueryRequest.class);
        GoodsCateListByConditionResponse response = new GoodsCateListByConditionResponse();
        response.setGoodsCateVOList(KsBeanUtil.convert(retailGoodsCateService.queryRetailGoodsCate(queryRequest), GoodsCateVO.class));
        return BaseResponse.success(response);
    }
}
