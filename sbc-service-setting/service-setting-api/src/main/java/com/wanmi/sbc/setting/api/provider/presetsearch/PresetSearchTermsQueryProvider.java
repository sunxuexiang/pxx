package com.wanmi.sbc.setting.api.provider.presetsearch;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.presetsearch.PresetSearchTermsDeleteByIdRequest;
import com.wanmi.sbc.setting.api.response.presetsearch.PresetSearchTermsQueryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>预置搜索词VO</p>
 * @author weiwenhao
 * @date 2020-04-16
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "PresetSearchTermsQueryProvider")
public interface PresetSearchTermsQueryProvider {

    /**
     * 查询预置搜索词
     */
    @PostMapping("/setting/${application.setting.version}/preset_search_terms/list")
    BaseResponse<PresetSearchTermsQueryResponse> listPresetSearchTerms();


    /**
     * 删除预置搜索词
     */
    @PostMapping("/setting/${application.setting.version}/preset_search_terms/delete-by-id")
    BaseResponse delSearchTermsById(@RequestBody @Valid PresetSearchTermsDeleteByIdRequest request);

}
