package com.wanmi.sbc.searchterms.presetsearch;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.presetsearch.PresetSearchTermsQueryProvider;
import com.wanmi.sbc.setting.api.provider.presetsearch.PresetSearchTermsSaveProvider;
import com.wanmi.sbc.setting.api.request.presetsearch.PresetSearchTermsDeleteByIdRequest;
import com.wanmi.sbc.setting.api.request.presetsearch.PresetSearchTermsModifyNameRequest;
import com.wanmi.sbc.setting.api.request.presetsearch.PresetSearchTermsModifyRequest;
import com.wanmi.sbc.setting.api.request.presetsearch.PresetSearchTermsRequest;
import com.wanmi.sbc.setting.api.response.presetsearch.PresetSearchTermsQueryResponse;
import com.wanmi.sbc.setting.api.response.presetsearch.PresetSearchTermsResponse;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 新增预置搜索词
 */
@Api(tags = "PresetSearchTermsSaveController", description = "预置搜索词服务API")
@RestController
@ApiModel
@RequestMapping(value = "/preset_search_terms")
public class PresetSearchTermsSaveController {


    @Autowired
    private PresetSearchTermsSaveProvider searchTermsSaveProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private PresetSearchTermsQueryProvider presetSearchTermsProvider;


    @ApiOperation(value = "新增预置搜索词")
    @PostMapping("/add")
    public BaseResponse<PresetSearchTermsResponse> add(@RequestBody @Valid PresetSearchTermsRequest request) {
        operateLogMQUtil.convertAndSend("预置搜索词","新增预置热门搜索词","新增热门搜索词："+request.getPresetSearchKeyword());
        return searchTermsSaveProvider.add(request);
    }

    /**
     * 编辑预置搜索词
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑预置搜索词")
    @PostMapping("/modify")
    BaseResponse modify(@RequestBody @Valid PresetSearchTermsModifyRequest request){
        operateLogMQUtil.convertAndSend("预置搜索词", "编辑预置热门搜索词", "编辑热门搜索词重新排序");
        return searchTermsSaveProvider.modify(request);
    }

    /**
     * 编辑预置搜索词
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑预置搜索词")
    @PutMapping("/modifyName")
    BaseResponse modifyName(@RequestBody @Valid PresetSearchTermsModifyNameRequest request){
        operateLogMQUtil.convertAndSend("预置搜索词", "编辑预置搜索词", "编辑搜索词重新排序");
        return searchTermsSaveProvider.modifyName(request);

    }

    /**
     * 查询预置搜索词
     * @return
     */
    @ApiOperation(value = "查询预置搜索词")
    @PostMapping("/list")
    public BaseResponse<PresetSearchTermsQueryResponse> listPresetSearchTerms() {
        return presetSearchTermsProvider.listPresetSearchTerms();
    }

    /**
     * 根据id删除
     * @param request
     * @return
     */
    @DeleteMapping("/delete")
    public BaseResponse deleteById(@RequestBody @Valid PresetSearchTermsDeleteByIdRequest request) {
        operateLogMQUtil.convertAndSend("预置搜索词", "根据id删除", "根据id删除搜索词");
        return presetSearchTermsProvider.delSearchTermsById(request);
    }

}
