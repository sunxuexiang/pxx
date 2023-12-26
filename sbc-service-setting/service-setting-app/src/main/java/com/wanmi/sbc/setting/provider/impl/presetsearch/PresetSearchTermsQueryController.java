package com.wanmi.sbc.setting.provider.impl.presetsearch;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.presetsearch.PresetSearchTermsQueryProvider;
import com.wanmi.sbc.setting.api.request.presetsearch.PresetSearchTermsDeleteByIdRequest;
import com.wanmi.sbc.setting.api.response.presetsearch.PresetSearchTermsQueryResponse;
import com.wanmi.sbc.setting.presetsearch.service.PresetSearchTermsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * 新增预置搜索词
 */
@RestController
public class PresetSearchTermsQueryController implements PresetSearchTermsQueryProvider {

    @Autowired
    private PresetSearchTermsService presetSearchTermsService;

    @Override
    public BaseResponse<PresetSearchTermsQueryResponse> listPresetSearchTerms() {
        PresetSearchTermsQueryResponse presetSearchTermsQueryResponse = presetSearchTermsService.listPresetSearchTerms();
        presetSearchTermsQueryResponse.setPresetSearchTermsVO(presetSearchTermsQueryResponse.getPresetSearchTermsVO().stream()
                .sorted((a,b)->a.getSort().compareTo(b.getSort()))
                .collect(Collectors.toList()));
        return BaseResponse.success(presetSearchTermsQueryResponse);
    }

    @Override
    public BaseResponse delSearchTermsById(@Valid PresetSearchTermsDeleteByIdRequest request) {
        presetSearchTermsService.deleteById(request.getId());
        return BaseResponse.SUCCESSFUL();
    }
}
