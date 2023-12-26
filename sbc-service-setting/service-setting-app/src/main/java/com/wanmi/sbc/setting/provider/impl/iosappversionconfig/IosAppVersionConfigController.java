package com.wanmi.sbc.setting.provider.impl.iosappversionconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.iosappversionconfig.IosAppVersionConfigProvider;
import com.wanmi.sbc.setting.api.request.iosappversionconfig.IosAppVersionConfigAddRequest;
import com.wanmi.sbc.setting.api.request.iosappversionconfig.IosAppVersionConfigPageRequest;
import com.wanmi.sbc.setting.api.response.iosappversionconfig.IosAppVersionConfigPageResponse;
import com.wanmi.sbc.setting.api.response.iosappversionconfig.IosAppVersionConfigResponse;
import com.wanmi.sbc.setting.bean.vo.IosAppVersionConfigVO;
import com.wanmi.sbc.setting.iosappversionconfig.model.root.IosAppVersionConfig;
import com.wanmi.sbc.setting.iosappversionconfig.service.IosAppVersionConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: Ios版本配置管理请求Controller
 * @author: jiangxin
 * @create: 2021-09-15 17:13
 */
@RestController
@Validated
public class IosAppVersionConfigController implements IosAppVersionConfigProvider {
    @Autowired
    private IosAppVersionConfigService iosAppVersionConfigService;

    @Override
    public BaseResponse add(IosAppVersionConfigAddRequest addRequest) {
        addRequest.setLastVersionUpdateTime(LocalDateTime.now());
        IosAppVersionConfig config = KsBeanUtil.convert(addRequest, IosAppVersionConfig.class);
        return BaseResponse.success(iosAppVersionConfigService.add(config));
    }


    @Override
    public BaseResponse<IosAppVersionConfigResponse> getVersionInfo(String versionNo, Long buildNo) {
        //根据版本号和构建号获取版本信息
        IosAppVersionConfig iosAppVersionConfig = iosAppVersionConfigService.getIosAppVersionConfigByVersionNoAndBuildNo(versionNo, buildNo);
        IosAppVersionConfigVO iosAppVersionConfigVO = KsBeanUtil.convert(iosAppVersionConfig, IosAppVersionConfigVO.class);
        //获取最新的版本号
        String lastVersionNo = iosAppVersionConfigService.getLastVersionNo();
        iosAppVersionConfigVO.setLastVersionNo(lastVersionNo);
        List<IosAppVersionConfigVO> iosAppVersionConfigVOList = new ArrayList<>();
        iosAppVersionConfigVOList.add(iosAppVersionConfigVO);
        return BaseResponse.success(IosAppVersionConfigResponse.builder().iosAppVersionConfigVOS(iosAppVersionConfigVOList).build());
    }

    @Override
    public BaseResponse<IosAppVersionConfigVO> getVersionInfoById(Long id) {
        IosAppVersionConfig iosAppVersionConfig = iosAppVersionConfigService.getById(id);
        IosAppVersionConfigVO iosAppVersionConfigVO = KsBeanUtil.convert(iosAppVersionConfig,IosAppVersionConfigVO.class);
        return BaseResponse.success(iosAppVersionConfigVO);
    }

    @Override
    public BaseResponse<IosAppVersionConfigPageResponse> getListVersions(IosAppVersionConfigPageRequest request) {
        return BaseResponse.success(iosAppVersionConfigService.page(request));
    }
}
