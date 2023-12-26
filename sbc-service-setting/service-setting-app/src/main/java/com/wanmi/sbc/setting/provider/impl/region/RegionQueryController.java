package com.wanmi.sbc.setting.provider.impl.region;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.region.RegionQueryProvider;
import com.wanmi.sbc.setting.api.response.region.RegionQueryResponse;
import com.wanmi.sbc.setting.bean.vo.RegionCopyVO;
import com.wanmi.sbc.setting.bean.vo.RegionVO;
import com.wanmi.sbc.setting.region.service.RegionQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 城市编号
 *
 * @author yitang
 * @version 1.0
 */
@RestController
@Validated
public class RegionQueryController implements RegionQueryProvider {

    @Autowired
    private RegionQueryService regionQueryService;

    @Override
    public BaseResponse<List<RegionVO>> queryRegionNumber(@RequestBody @Valid RegionQueryResponse response){
        return regionQueryService.getRegionNumber(response.getNumber());
    }

    @Override
    public BaseResponse<List<RegionCopyVO>> queryRegionCopyNumber(@RequestBody @Valid RegionQueryResponse response){
        return regionQueryService.getRegionCopyNumber(response.getNumber());
    }
}
