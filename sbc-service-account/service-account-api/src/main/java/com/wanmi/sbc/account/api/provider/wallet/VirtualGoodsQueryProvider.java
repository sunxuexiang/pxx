package com.wanmi.sbc.account.api.provider.wallet;

import com.wanmi.sbc.account.api.request.wallet.VirtualGoodsRequest;
import com.wanmi.sbc.account.api.response.wallet.VirtualGoodsResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author jeffrey
 * @create 2021-08-21 17:08
 */

/**
 * 虚拟商品API
 */
@FeignClient(value = "${application.account.name}", url="${feign.url.account:#{null}}", contextId = "VirtualGoodsQueryProvider")
public interface VirtualGoodsQueryProvider {

    /**
     * 查询后台虚拟商品(根据id查询)
     */
    @PostMapping("/account/${application.account.version}/wallet/virtualGoods/getOne")
    BaseResponse<VirtualGoodsResponse> getVirtualGoods(@RequestBody VirtualGoodsRequest virtualGoodsRequest);

    /**
     * 查询后台虚拟商品(根据多个id查询)
     */
    @PostMapping("/account/${application.account.version}/wallet/virtualGoods/list")
    BaseResponse<VirtualGoodsResponse> getVirtualGoodsList(@RequestBody VirtualGoodsRequest virtualGoodsRequest);

    /**
     * 查询后台虚拟商品(分页查询)
     */
    @PostMapping("/account/${application.account.version}/wallet/virtualGoods/allList")
    BaseResponse<VirtualGoodsResponse> getPageVirtualGoodsList(@RequestBody VirtualGoodsRequest request);

    /**
     * 新增或者修改后台虚拟商品
     */
    @PutMapping("/account/${application.account.version}/wallet/virtualGoods")
    BaseResponse putVirtualGoods(@RequestBody @Valid VirtualGoodsRequest request);

    /**
     * 删除一个虚拟商品,根据goodsId
     */
    @PostMapping("/account/${application.account.version}/wallet/deleteVirtualGoods")
    BaseResponse deleteVirtualGoodsByGoodsId(@RequestBody VirtualGoodsRequest request);

    /**
     * 删除多个虚拟商品,根据多个goodsId
     */
    @PostMapping("/account/${application.account.version}/wallet/virtualGoods/deleteGoodsIdList")
    BaseResponse deleteVirtualGoodsByGoodsIds(@RequestBody VirtualGoodsRequest request);

}
