package com.wanmi.sbc.marketing.api.provider.pile;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.marketing.api.request.pile.*;
import com.wanmi.sbc.marketing.bean.vo.PileActivityGoodsPageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p></p>
 * author: chenchang
 * Date: 2022-09-19
 */
@FeignClient(value = "${application.marketing.name}", url="${feign.url.marketing:#{null}}", contextId = "PileActivityGoodsProvider")
public interface PileActivityGoodsProvider {
    /**
     * 创建活动商品
     * @param request 创建活动商品请求结构 {@link PileActivityAddRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/pile/activity/goods/add")
    BaseResponse add(@RequestBody @Valid PileActivityGoodsAddRequest request);

    /**
     * 编辑活动商品
     * @param request 编辑活动商品请求结构 {@link PileActivityGoodsModifyRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/pile/activity/goods/modify")
    BaseResponse modify(@RequestBody @Valid PileActivityGoodsModifyRequest request);

    /**
     * 删除活动商品
     * @param request 删除活动商品请求结构 {@link PileActivityGoodsDeleteRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/pile/activity/goods/delete")
    BaseResponse delete(@RequestBody @Valid PileActivityGoodsDeleteRequest request);

    /**
     * 分页搜索活动商品
     * @param request 分页搜索活动商品请求结构 {@link PileActivityGoodsPageRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/pile/activity/goods/page")
    BaseResponse<MicroServicePage<PileActivityGoodsPageVO>> page(@RequestBody @Valid PileActivityGoodsPageRequest request);

}
