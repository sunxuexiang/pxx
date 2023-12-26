package com.wanmi.sbc.marketing.api.provider.suittobuy;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.marketing.api.request.suittobuy.SuitToBuyAddRequest;
import com.wanmi.sbc.marketing.api.request.suittobuy.SuitToBuyModifyRequest;
import com.wanmi.sbc.marketing.api.response.suittobuy.SuitToBuyAddResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @Description: 套装购买
 * @Author: XinJiang
 * @Date: 2022/2/4 15:38
 */
@FeignClient(value = "${application.marketing.name}", url="${feign.url.marketing:#{null}}", contextId = "SuitToBuyProvider")
public interface SuitToBuyProvider {

    @PostMapping("/marketing/${application.marketing.version}/suit-to-buy/add")
    BaseResponse<List<String>> add(@RequestBody @Valid SuitToBuyAddRequest addRequest);

    @PostMapping("/marketing/${application.marketing.version}/suit-to-buy/modify")
    BaseResponse modify(@RequestBody @Valid SuitToBuyModifyRequest modifyRequest);
}
