package com.wanmi.sbc.presetsearch;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.setting.api.provider.presetsearch.PresetSearchTermsQueryProvider;
import com.wanmi.sbc.setting.api.response.presetsearch.PresetSearchTermsQueryResponse;
import com.wanmi.sbc.setting.bean.vo.PresetSearchTermsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

/**
 * 查询预置搜索词
 */
@Api(tags = "PresetSearchTermsQueryController", description = "预置搜索词服务API")
@RestController
@RequestMapping("/preset_search_terms")
@Validated
public class PresetSearchTermsQueryController {

    @Autowired
    private PresetSearchTermsQueryProvider presetSearchTermsProvider;

    @Autowired
    private RedisService redisService;

    /**
     * 查询预置搜索词
     * @return
     */
    @ApiOperation(value = "查询预置搜索词")
    @PostMapping("/list")
    public BaseResponse<PresetSearchTermsQueryResponse> listPresetSearchTerms() {

        List<PresetSearchTermsVO> list;
        if (redisService.hasKey(RedisKeyConstant.PRESET_SEARCH_TERMS_DATA)) {
            list= redisService.getList(RedisKeyConstant.PRESET_SEARCH_TERMS_DATA, PresetSearchTermsVO.class);
        }else {
            list = presetSearchTermsProvider.listPresetSearchTerms().getContext().getPresetSearchTermsVO();
            redisService.setObj(RedisKeyConstant.PRESET_SEARCH_TERMS_DATA, list, Duration.ofDays(7).getSeconds());
        }
        return BaseResponse.success(PresetSearchTermsQueryResponse.builder().presetSearchTermsVO(list).build());
    }
}
