package com.wanmi.sbc.goods.api.provider.goodsattributekey;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodsattribute.GoodsAttributeAddRequest;
import com.wanmi.sbc.goods.api.request.goodsattribute.GoodsAttributeQueryRequest;
import com.wanmi.sbc.goods.api.request.goodsattributekey.GoodsAttributeKeyQueryRequest;
import com.wanmi.sbc.goods.api.request.spec.GoodsSpecListByGoodsIdsRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateGoodsRelaListByGoodsIdsRequest;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributeAddResponse;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributeListResponse;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributePageResponse;
import com.wanmi.sbc.goods.api.response.goodsattributekey.GoodsAttributeKeyListResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsAttributeKeyVO;
import com.wanmi.sbc.goods.bean.vo.GoodsAttributeVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>商品属性关联调用</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@FeignClient(value = "${application.goods.name}", contextId = "GoodsAttributeKeyQueryProvider")
public interface GoodsAttributeKeyQueryProvider {



    /**
     *  集合数据
     *
     * @param request {@link GoodsAttributeAddRequest}
     * @return 修改结果 {@link GoodsAttributeAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsAttributeKey/getList")
    BaseResponse<GoodsAttributeKeyListResponse> getList(@RequestBody @Valid GoodsAttributeKeyQueryRequest request);
    /**
     *  集合数据
     *
     * @param request {@link GoodsAttributeAddRequest}
     * @return 修改结果 {@link GoodsAttributeAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsAttributeKey/listByGoodsIds")
    BaseResponse <GoodsAttributeKeyListResponse> listByGoodsIds(@RequestBody @Valid StoreCateGoodsRelaListByGoodsIdsRequest request);


    /**
     *  集合数据
     *
     * @param request {@link GoodsAttributeAddRequest}
     * @return 修改结果 {@link GoodsAttributeAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsAttributeKey/listByGoodsInfoIds")
    BaseResponse <GoodsAttributeKeyListResponse> listByGoodsInfoIds(@RequestBody @Valid StoreCateGoodsRelaListByGoodsIdsRequest request);

    /**
     *  集合数据
     *
     * @param request {@link GoodsAttributeAddRequest}
     * @return 修改结果 {@link GoodsAttributeAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsAttributeKey/arrListByGoodsIds")
    BaseResponse <GoodsAttributeKeyListResponse> arrListByGoodsIds(@RequestBody @Valid StoreCateGoodsRelaListByGoodsIdsRequest request);




    /**
     *  集合数据-查询值一定不能为空
     *
     * @param request {@link GoodsAttributeAddRequest}
     * @return 修改结果 {@link GoodsAttributeAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsAttributeKey/getNotGoodsList")
    BaseResponse<GoodsAttributeKeyListResponse> getNotGoodsList(@RequestBody @Valid GoodsAttributeKeyQueryRequest request);



}
