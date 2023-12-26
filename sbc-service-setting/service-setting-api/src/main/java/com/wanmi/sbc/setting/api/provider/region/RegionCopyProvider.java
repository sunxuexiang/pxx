package com.wanmi.sbc.setting.api.provider.region;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.RegionCopyRequest;
import com.wanmi.sbc.setting.api.request.yunservice.RegionCopyQueryCodeRequest;
import com.wanmi.sbc.setting.api.response.region.RegionQueryResponse;
import com.wanmi.sbc.setting.bean.vo.RegionCopyFrontVO;
import com.wanmi.sbc.setting.bean.vo.RegionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * 城市编码
 *
 * @author yitang
 * @version 1.0
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "RegionCopyProvider")
public interface RegionCopyProvider {
    /**
     * 查询城市编码
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/region/addList")
    BaseResponse addList(@RequestBody @Valid RegionCopyRequest request);


    /**
     * 根据编码查询城市联动
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/region/queryByCode")
    BaseResponse queryByCode(@RequestBody @Valid RegionCopyQueryCodeRequest request);




    /**
     * 根据编码查询名称
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/region/queryByCodeAndReturn")
    BaseResponse<String> queryByCodeAndReturn(@RequestBody @Valid RegionCopyQueryCodeRequest request);


    @GetMapping("/setting/${application.setting.version}/region/getFrontJsonByLevel")
    BaseResponse<List<RegionCopyFrontVO>> getFrontJsonByLevel(@RequestParam(value = "level") Integer level);

}
