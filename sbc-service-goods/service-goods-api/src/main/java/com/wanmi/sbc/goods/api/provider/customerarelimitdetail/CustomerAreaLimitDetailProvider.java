package com.wanmi.sbc.goods.api.provider.customerarelimitdetail;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.customerarealimitdetail.CustomerAreaLimitDetailAddRequest;
import com.wanmi.sbc.goods.api.request.customerarealimitdetail.CustomerAreaLimitDetailRequest;
import com.wanmi.sbc.goods.api.response.customerarealimitdetail.CustomerAreaLimitDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;


@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "CustomerAreaLimitDetailProvider")
public interface CustomerAreaLimitDetailProvider {


    /**
     * 根据用户id和商品id和地区id获取用户今日购买数据
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/CustomerAreaLimitDetail/list-by-cid")
    BaseResponse<CustomerAreaLimitDetailResponse> listByCids(@RequestBody @Valid CustomerAreaLimitDetailRequest request);



    /**
     * 区域限购新增数据
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/CustomerAreaLimitDetail/add-order")
    BaseResponse addByOrder(@RequestBody @Valid CustomerAreaLimitDetailAddRequest request);

}
