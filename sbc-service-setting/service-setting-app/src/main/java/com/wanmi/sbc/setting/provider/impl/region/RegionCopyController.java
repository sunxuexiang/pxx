package com.wanmi.sbc.setting.provider.impl.region;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.region.RegionCopyProvider;
import com.wanmi.sbc.setting.api.provider.region.RegionQueryProvider;
import com.wanmi.sbc.setting.api.request.RegionCopyRequest;
import com.wanmi.sbc.setting.api.request.yunservice.RegionCopyQueryCodeRequest;
import com.wanmi.sbc.setting.api.response.region.RegionQueryResponse;
import com.wanmi.sbc.setting.bean.vo.RegionCopyFrontVO;
import com.wanmi.sbc.setting.bean.vo.RegionVO;
import com.wanmi.sbc.setting.region.model.RegionCopy;
import com.wanmi.sbc.setting.region.repository.RegionCopyRepository;
import com.wanmi.sbc.setting.region.service.RegionCopyService;
import com.wanmi.sbc.setting.region.service.RegionQueryService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RegionCopyController implements RegionCopyProvider {

    @Autowired
    private RegionCopyService regionCopyService;

    @Autowired
    private RegionCopyRepository regionCopyRepository;

    @Override
    public BaseResponse addList(@RequestBody RegionCopyRequest request){
        return regionCopyService.addList(KsBeanUtil.convertList(request.getRegionCopyVOList(), RegionCopy.class));
    }

    @Override
    public BaseResponse queryByCode(RegionCopyQueryCodeRequest request) {
        return regionCopyService.queryRegionByCode(request.getCode());
    }

    @Override
    public BaseResponse<String> queryByCodeAndReturn(RegionCopyQueryCodeRequest request) {
        List<RegionCopy> regionCopy = regionCopyRepository.getRegionCopyByCode(request.getCode());
        return BaseResponse.success(regionCopy.stream().findAny().get().getName());
    }

    @Override
    public BaseResponse<List<RegionCopyFrontVO>> getFrontJsonByLevel(Integer level) {
        List<RegionCopyFrontVO> regionCopyFrontVOS= regionCopyService.getFrontJsonByLevel(level);
        return BaseResponse.success(regionCopyFrontVOS);
    }
}
