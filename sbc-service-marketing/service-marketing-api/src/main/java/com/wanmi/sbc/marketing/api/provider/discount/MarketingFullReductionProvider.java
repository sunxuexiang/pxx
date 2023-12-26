package com.wanmi.sbc.marketing.api.provider.discount;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullReductionAddRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullReductionModifyRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullReductionSaveLevelListRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>营销满减业务</p>
 * author: sunkun
 * Date: 2018-11-21
 */
@FeignClient(value = "${application.marketing.name}", url="${feign.url.marketing:#{null}}", contextId = "MarketingFullReductionProvider")
public interface MarketingFullReductionProvider {

    /**
     * 新增满减
     * @param request 新增满减请求结构 {@link MarketingFullReductionAddRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/full/reduction/add")
    BaseResponse<List<String>> add(@RequestBody @Valid MarketingFullReductionAddRequest request);

    /**
     * 新增满赠数据
     * @param saveOrUpdateMarketingRequest
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/full/reduction/save-or-update")
    BaseResponse<MarketingResponse> saveOrUpdateMarketing(@RequestBody @Valid SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest);

    
    /**
     * 修改满减
     * @param request 修改满减请求结构 {@link MarketingFullReductionModifyRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/full/reduction/modify")
    BaseResponse modify(@RequestBody @Valid MarketingFullReductionModifyRequest request);

    /**
     * 保存多级优惠信息
     * @param request 保存多级优惠信息请求结构 {@link MarketingFullReductionSaveLevelListRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/full/reduction/save-level-list")
    BaseResponse saveLevelList(@RequestBody @Valid MarketingFullReductionSaveLevelListRequest request);
}
