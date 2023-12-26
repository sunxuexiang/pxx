package com.wanmi.sbc.goods.api.provider.cate;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateAddRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateBatchModifySortRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Description: 散批商品分类操作接口
 * @Author: XinJiang
 * @Date: 2022/5/5 15:50
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "RetailGoodsCateProvider")
public interface RetailGoodsCateProvider {

    /**
     * 新增散批推荐商品分类
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/cate/add-retail-goods-cate")
    BaseResponse addRetailGoodsCate(@RequestBody @Valid GoodsCateAddRequest request);

    /**
     * 删除散批推荐商品分类
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/cate/del-retail-goods-cate")
    BaseResponse delRetailGoodsCate(@RequestBody @Valid GoodsCateAddRequest request);

    /**
     * 批量修改散批推荐分类排序
     *
     * @param request 批量分类排序信息结构 {@link GoodsCateBatchModifySortRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/goods/${application.goods.version}/cate/retail-goods-cate-modify-sort")
    BaseResponse batchRetailGoodsCateModifySort(@RequestBody @Valid GoodsCateBatchModifySortRequest request);
}
