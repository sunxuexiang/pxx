package com.wanmi.sbc.marketing.api.provider.pile;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityDetailByIdRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityGetByIdRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityPageRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityPageResponse;
import com.wanmi.sbc.marketing.api.response.pile.PileActivityDetailByIdResponse;
import com.wanmi.sbc.marketing.api.response.pile.PileActivityGetByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p></p>
 * author: chenchang
 * Date: 2022-09-06
 */
@FeignClient(value = "${application.marketing.name}", url="${feign.url.marketing:#{null}}", contextId = "PileActivityQueryProvider")
public interface PileActivityQueryProvider {

    /**
     * 根据id查询囤货活动详情信息
     *
     * @param request 包含id的查询详情请求结构 {@link PileActivityDetailByIdRequest}
     * @return 囤货活动详情信息 {@link PileActivityDetailByIdResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/pile/info/get-detail-by-id")
    BaseResponse<PileActivityDetailByIdResponse> getDetailById(@RequestBody @Valid PileActivityDetailByIdRequest request);

    /**
     * 查询活动列表
     * @param request 查询活动列表请求结构 {@link PileActivityPageRequest}
     * @return {@link PileActivityPageResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/pile/activity/page")
    BaseResponse<PileActivityPageResponse> page(@RequestBody @Valid PileActivityPageRequest request);

    /**
     * 通过主键获取囤货活动
     * @param request 通过主键获取囤货活动请求结构 {@link PileActivityGetByIdRequest}
     * @return {@link PileActivityGetByIdResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/pile/activity/get-by-id")
    BaseResponse<PileActivityGetByIdResponse> getById(@RequestBody @Valid PileActivityGetByIdRequest request);


}
