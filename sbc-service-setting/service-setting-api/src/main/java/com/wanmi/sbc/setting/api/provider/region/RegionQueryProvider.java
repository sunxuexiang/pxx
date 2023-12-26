package com.wanmi.sbc.setting.api.provider.region;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.response.region.RegionQueryResponse;
import com.wanmi.sbc.setting.bean.vo.RegionCopyVO;
import com.wanmi.sbc.setting.bean.vo.RegionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 城市编码
 *
 * @author yitang
 * @version 1.0
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "RegionQueryProvider")
public interface RegionQueryProvider {
    /**
     * 查询城市编码
     * @param response
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/region/query_region_number")
    BaseResponse<List<RegionVO>> queryRegionNumber(@RequestBody @Valid RegionQueryResponse response);

    /**
     * 查询城市编码
     * @param response
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/region/query_region_copy_number")
    BaseResponse<List<RegionCopyVO>> queryRegionCopyNumber(@RequestBody @Valid RegionQueryResponse response);

}
