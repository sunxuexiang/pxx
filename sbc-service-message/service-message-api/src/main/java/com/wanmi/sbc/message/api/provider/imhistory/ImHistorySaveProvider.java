package com.wanmi.sbc.message.api.provider.imhistory;

import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>报错im历史数据</p>
 * Created by sgy on 2023/7/1.
 */
@FeignClient(value = "${application.message.name}", url="${feign.url.message:#{null}}", contextId = "ImHistorySaveProvider")
public interface ImHistorySaveProvider {

    @PostMapping("/sms/${application.sms.version}/messagesend/imHistory/add")
    BaseResponse add(@RequestBody @Valid List<String> request);


}
