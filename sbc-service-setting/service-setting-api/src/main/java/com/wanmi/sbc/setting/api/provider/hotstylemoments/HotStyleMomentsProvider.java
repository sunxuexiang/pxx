package com.wanmi.sbc.setting.api.provider.hotstylemoments;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.hotstylemoments.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @Description: 爆款时刻操作接口
 * @Author: XinJiang
 * @Date: 2022/5/9 21:45
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "HotStyleMomentsProvider")
public interface HotStyleMomentsProvider {

    /**
     * 新增爆款时刻
     *
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/add/hot-style-comments")
    BaseResponse add(@RequestBody @Valid HotStyleMomentsAddRequest request);

    /**
     * 修改爆款时刻
     *
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/modify/hot-style-comments")
    BaseResponse modify(@RequestBody @Valid HotStyleMomentsModifyRequest request);

    /**
     * 删除爆款时刻
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/del-by-id")
    BaseResponse delById(@RequestBody @Valid HotStyleMomentsDelByIdRequest request);

    /**
     * 批量删除爆款时刻
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/del-by-ids")
    BaseResponse delByIds(@RequestBody @Valid HotStyleMomentsDelByIdRequest request);

    /**
     * 刷新缓存
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/fill-redis")
    BaseResponse fillRedis();

    /**
     * 终止活动
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/termination-by-id")
    BaseResponse terminationById(@RequestBody @Valid HotStyleMomentsTerminationRequest request);

    /**
     * 启动或暂停活动
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/pause-by-id")
    BaseResponse pauseById(@RequestBody @Valid HotStyleMomentsPauseRequest request);

    /**
     * 提前开始活动
     * @param hotId
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/early-start-by-id")
    BaseResponse earlyStart(@RequestParam("hotId") Long hotId);
}
