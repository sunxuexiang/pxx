package com.wanmi.sbc.setting.provider.impl.presetsearch;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.presetsearch.PresetSearchTermsSaveProvider;
import com.wanmi.sbc.setting.api.request.presetsearch.PresetSearchTermsModifyNameRequest;
import com.wanmi.sbc.setting.api.request.presetsearch.PresetSearchTermsModifyRequest;
import com.wanmi.sbc.setting.api.request.presetsearch.PresetSearchTermsRequest;
import com.wanmi.sbc.setting.api.response.presetsearch.PresetSearchTermsResponse;
import com.wanmi.sbc.setting.bean.vo.PresetSearchTermsVO;
import com.wanmi.sbc.setting.presetsearch.model.PresetSearchTerms;
import com.wanmi.sbc.setting.presetsearch.service.PresetSearchTermsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 新增预置搜索词
 */
@RestController
public class PresetSearchTermsSaveController implements PresetSearchTermsSaveProvider {

    @Autowired
    private PresetSearchTermsService presetSearchTermsService;

    @Override
    public BaseResponse<PresetSearchTermsResponse> add(@Valid PresetSearchTermsRequest request) {
        PresetSearchTerms presetSearchTerms = new PresetSearchTerms();
        KsBeanUtil.copyPropertiesThird(request, presetSearchTerms);
        PresetSearchTermsVO presetSearchTermsVO= wrapperVo(presetSearchTermsService.add(presetSearchTerms));
        return BaseResponse.success(new PresetSearchTermsResponse(presetSearchTermsVO));
    }

    /**
     * 将实体包装成VO
     *
     * @author lq
     */
    public PresetSearchTermsVO wrapperVo(PresetSearchTerms presetSearchTerms) {
        if (presetSearchTerms != null) {
            PresetSearchTermsVO presetSearchTermsVO = new PresetSearchTermsVO();
            KsBeanUtil.copyPropertiesThird(presetSearchTerms, presetSearchTermsVO);
            return presetSearchTermsVO;
        }
        return null;
    }

    @Override
    public BaseResponse modify(@Valid PresetSearchTermsModifyRequest request) {
        List<PresetSearchTerms> presetSearchTermsList = KsBeanUtil.convert(request.getPresetSearchTermsList(), PresetSearchTerms.class);
        presetSearchTermsService.modifySort(presetSearchTermsList);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modifyName(@Valid PresetSearchTermsModifyNameRequest request) {
        PresetSearchTerms presetSearchTerms = new PresetSearchTerms();
        presetSearchTerms.setId(request.getId());
        presetSearchTerms.setPresetSearchKeyword(request.getPresetSearchKeyword());
        presetSearchTermsService.modifyName(presetSearchTerms);
        return BaseResponse.SUCCESSFUL();
    }

}
