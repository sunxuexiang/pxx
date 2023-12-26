package com.wanmi.sbc.marketing.api.provider.market;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.marketing.api.request.market.*;
import com.wanmi.sbc.marketing.api.response.market.MarketingAddResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingDeleteResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingPauseResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingStartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Author: ZhangLingKe
 * @Description: 营销更新接口Feign客户端
 * @Date: 2018-11-16 16:56
 */
@FeignClient(value = "${application.marketing.name}", url="${feign.url.marketing:#{null}}", contextId = "MarketingProvider")
public interface MarketingProvider {

    /**
     * 营销活动中的数据同步到Redis缓存中
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/sync-cache-from-persistence")
    BaseResponse syncCacheFromPersistence();
    /**
     * 新增营销数据
     * @param addRequest 新增参数 {@link MarketingAddRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/add")
    BaseResponse<List<String>> add(@RequestBody @Valid MarketingAddRequest addRequest);

    /**
     * 修改营销数据
     * @param modifyRequest 新增参数 {@link MarketingModifyRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/modify")
    BaseResponse modify(@RequestBody @Valid MarketingModifyRequest modifyRequest);

    /**
     * 删除营销数据
     * @param deleteByIdRequest 营销ID {@link MarketingDeleteByIdRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/delete-by-id")
    BaseResponse<MarketingDeleteResponse> deleteById(@RequestBody @Valid MarketingDeleteByIdRequest deleteByIdRequest);

    /**
     * 暂停营销
     * @param pauseByIdRequest 营销ID {@link MarketingPauseByIdRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/pause-by-id")
    BaseResponse<MarketingPauseResponse> pauseById(@RequestBody @Valid MarketingPauseByIdRequest pauseByIdRequest);

    /**
     * 启动营销
     * @param startByIdRequest 营销ID {@link MarketingStartByIdRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/start-by-id")
    BaseResponse<MarketingStartResponse> startById(@RequestBody @Valid MarketingStartByIdRequest startByIdRequest);

    /**
     * 终止营销
     * @param startByIdRequest 营销ID {@link MarketingStartByIdRequest}
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/termination-by-id")
    BaseResponse terminationMarketingById(@RequestBody @Valid MarketingStartByIdRequest startByIdRequest);

    /**
     * 营销活动赠品原子自减
     * @param
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/incrActiveNum-by-id")
    BaseResponse<Map<String,Object>> incrActiveNum(@RequestBody @Valid MarketingDecrNumRequest marketingDecrNumRequest);

}
