package com.wanmi.sbc.marketing.api.provider.discount;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullDiscountAddRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullDiscountModifyRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullDiscountSaveLevelListRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>营销满折业务</p>
 * author: sunkun
 * Date: 2018-11-20
 */
@FeignClient(value = "${application.marketing.name}", url="${feign.url.marketing:#{null}}", contextId = "MarketingFullDiscountProvider")
public interface MarketingFullDiscountProvider {

    /**
     * 新增满折
     * @param request 新增满折请求结构 {@link MarketingFullDiscountAddRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/full/discount/add")
    BaseResponse<List<String>> add(@RequestBody @Valid MarketingFullDiscountAddRequest request);

    /**
     * 新增满折
     * @param saveOrUpdateMarketingRequest
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/full/discount/save-or-update")
    BaseResponse<MarketingResponse> saveOrUpdateMarketing(@RequestBody @Valid SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest);
    /**
     * 修改满折
     * @param request 修改满折请求结构 {@link MarketingFullDiscountModifyRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/full/discount/modify")
    BaseResponse modify(@RequestBody @Valid MarketingFullDiscountModifyRequest request);

    /**
     * 保存多级优惠信息
     * @param request 保存多级优惠信息请求结构 {@link MarketingFullDiscountSaveLevelListRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/full/discount/save-level-list")
    BaseResponse saveLevelList(@RequestBody @Valid MarketingFullDiscountSaveLevelListRequest request);
}
