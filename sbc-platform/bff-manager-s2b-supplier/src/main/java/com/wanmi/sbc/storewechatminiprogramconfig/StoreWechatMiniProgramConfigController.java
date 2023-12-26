package com.wanmi.sbc.storewechatminiprogramconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.setting.api.provider.saas.SaasSettingQueryProvider;
import com.wanmi.sbc.setting.api.provider.storewechatminiprogramconfig.StoreWechatMiniProgramConfigQueryProvider;
import com.wanmi.sbc.setting.api.provider.storewechatminiprogramconfig.StoreWechatMiniProgramConfigSaveProvider;
import com.wanmi.sbc.setting.api.request.storewechatminiprogramconfig.StoreWechatMiniProgramConfigAddRequest;
import com.wanmi.sbc.setting.api.request.storewechatminiprogramconfig.StoreWechatMiniProgramConfigByStoreIdRequest;
import com.wanmi.sbc.setting.api.request.storewechatminiprogramconfig.StoreWechatMiniProgramConfigModifyRequest;
import com.wanmi.sbc.setting.api.response.storewechatminiprogramconfig.StoreWechatMiniProgramConfigAddResponse;
import com.wanmi.sbc.setting.api.response.storewechatminiprogramconfig.StoreWechatMiniProgramConfigByStoreIdResponse;
import com.wanmi.sbc.setting.api.response.storewechatminiprogramconfig.StoreWechatMiniProgramConfigModifyResponse;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;


@Api(description = "门店微信小程序配置管理API", tags = "StoreWechatMiniProgramConfigController")
@RestController
@RequestMapping(value = "/storewechatminiprogramconfig")
public class StoreWechatMiniProgramConfigController {

    @Autowired
    private StoreWechatMiniProgramConfigQueryProvider storeWechatMiniProgramConfigQueryProvider;

    @Autowired
    private StoreWechatMiniProgramConfigSaveProvider storeWechatMiniProgramConfigSaveProvider;

    @Autowired
    private SaasSettingQueryProvider saasSettingQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    @ApiOperation(value = "查询门店微信小程序配置")
    @GetMapping("/getConfig")
    public BaseResponse<StoreWechatMiniProgramConfigByStoreIdResponse> getConfig() {
        return storeWechatMiniProgramConfigQueryProvider.getByStoreId(StoreWechatMiniProgramConfigByStoreIdRequest.builder()
                .storeId(commonUtil.getStoreId())
                .build());
    }

    @ApiOperation(value = "新增门店微信小程序配置")
    @PostMapping("/add")
    public BaseResponse<StoreWechatMiniProgramConfigAddResponse> add(@RequestBody @Valid StoreWechatMiniProgramConfigAddRequest addReq) {
        operateLogMQUtil.convertAndSend("门店微信小程序配置管理", "新增门店微信小程序配置", "新增门店微信小程序配置");
        addReq.setDelFlag(DeleteFlag.NO);
        addReq.setCreateTime(LocalDateTime.now());
        addReq.setStoreId(commonUtil.getStoreId());
        addReq.setCompanyInfoId(commonUtil.getCompanyInfoId());
        return storeWechatMiniProgramConfigSaveProvider.add(addReq);
    }

    @ApiOperation(value = "修改门店微信小程序配置")
    @PutMapping("/modify")
    public BaseResponse<StoreWechatMiniProgramConfigModifyResponse> modify(@RequestBody @Valid StoreWechatMiniProgramConfigModifyRequest modifyReq) {
        operateLogMQUtil.convertAndSend("门店微信小程序配置管理", "修改门店微信小程序配置", "修改门店微信小程序配置");
        modifyReq.setUpdateTime(LocalDateTime.now());
        return storeWechatMiniProgramConfigSaveProvider.modify(modifyReq);
    }
}
