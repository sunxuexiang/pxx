package com.wanmi.sbc.goods.api.provider.cate;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateListByConditionRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateListByConditionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Description: 散批商品分类查询接口
 * @Author: XinJiang
 * @Date: 2022/5/5 15:58
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "RetailGoodsCateQueryProvider")
public interface RetailGoodsCateQueryProvider {

    /**
     * 根据条件查询商品分类列表信息
     *
     * @param request {@link GoodsCateListByConditionRequest}
     * @return 商品分类列表信息 {@link GoodsCateListByConditionResponse}
     */
    @PostMapping("/goods/${application.goods.version}/retail/cate/list-by-condition")
    BaseResponse<GoodsCateListByConditionResponse> listByCondition(
            @RequestBody @Valid GoodsCateListByConditionRequest request);
}
