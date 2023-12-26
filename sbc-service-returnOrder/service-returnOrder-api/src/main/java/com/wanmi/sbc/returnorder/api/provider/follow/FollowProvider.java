package com.wanmi.sbc.returnorder.api.provider.follow;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.follow.FollowDeleteRequest;
import com.wanmi.sbc.returnorder.api.request.follow.FollowSaveRequest;
import com.wanmi.sbc.returnorder.api.request.follow.InvalidGoodsDeleteRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>商品收藏服务</p>
 * author: sunpeng
 * Date: 2018-12-5
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnFollowProvider")
public interface FollowProvider {

    /**
     *  新增商品收藏
     * @param request  {@link FollowSaveRequest}
     * @return   {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/follow/save")
    BaseResponse save(@RequestBody @Valid FollowSaveRequest request);

    /**
     * 取消商品收藏
     * @param request {@link FollowDeleteRequest}
     * @return   {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/follow/delete")
    BaseResponse delete(@RequestBody @Valid FollowDeleteRequest request);

    /**
     * 删除失效商品
     * @param request {@link InvalidGoodsDeleteRequest}
     * @return   {@link BaseResponse}
     */
    @PostMapping("/returnOrder/${application.order.version}/follow/delete-invalidgoods")
    BaseResponse deleteInvalidGoods(@RequestBody @Valid InvalidGoodsDeleteRequest request);



}
