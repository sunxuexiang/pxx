package com.wanmi.sbc.live.provider.impl.stream;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.live.api.provider.stream.LiveLanguageProvider;
import com.wanmi.sbc.live.api.response.stream.LiveLanguageResponse;
import com.wanmi.sbc.live.stream.model.root.LiveLanguage;
import com.wanmi.sbc.live.stream.service.LiveLanguageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class LiveLanguageController implements LiveLanguageProvider {

    @Autowired
    private LiveLanguageService liveLanguageService;

    @Override
    public BaseResponse getList() {
        List<LiveLanguage> languageList = liveLanguageService.getAll();
        List<LiveLanguageResponse> resultList = KsBeanUtil.convertList(languageList, LiveLanguageResponse.class);
        return BaseResponse.success(resultList);
    }
}
