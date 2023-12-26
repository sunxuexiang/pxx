package com.wanmi.sbc.setting.api.provider.headline;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.headline.HeadLineSaveRequest;
import com.wanmi.sbc.setting.api.response.headline.HeadLineResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/7 15:17
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "HeadLineProvider")
public interface HeadLineProvider {

    /**
     * 保存白鲸头条
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/headline/save")
    BaseResponse save(@RequestBody @Valid List<HeadLineSaveRequest> request);

    @GetMapping("/setting/${application.setting.version}/headline/get")
    BaseResponse<List<HeadLineResponse>> get();
}
