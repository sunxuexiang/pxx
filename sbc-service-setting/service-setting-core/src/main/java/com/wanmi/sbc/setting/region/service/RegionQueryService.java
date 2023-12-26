package com.wanmi.sbc.setting.region.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.bean.vo.RegionCopyVO;
import com.wanmi.sbc.setting.bean.vo.RegionVO;
import com.wanmi.sbc.setting.region.model.RegionCopy;
import com.wanmi.sbc.setting.region.model.RegionModel;
import com.wanmi.sbc.setting.region.repository.RegionCopyRepository;
import com.wanmi.sbc.setting.region.repository.RegionQueryRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Service("RegionQueryService")
@Slf4j
public class RegionQueryService {
    @Autowired
    private RegionQueryRepository regionQueryRepository;

    @Autowired
    private RegionCopyRepository regionCopyRepository;


    public BaseResponse<List<RegionVO>> getRegionNumber(List<Long> number){
        List<RegionVO> regionVOS = new ArrayList<>();
        List<RegionModel> regionModelList = regionQueryRepository.getRegionNumber(number);
        log.info("RegionQueryService.getRegionNumber regionModelList:{}", JSONObject.toJSONString(regionModelList));
        if (CollectionUtils.isNotEmpty(regionModelList)){
            regionVOS.addAll(KsBeanUtil.convertList(regionModelList,RegionVO.class));
        }
        return BaseResponse.success(regionVOS);
    }

    public BaseResponse<List<RegionCopyVO>> getRegionCopyNumber(List<Long> number){
        List<RegionCopyVO> regionVOS = new ArrayList<>();
        List<RegionCopy> regionModelList = regionCopyRepository.getRegionNumber(number);
        log.info("RegionQueryService.getRegionNumber regionModelList:{}", JSONObject.toJSONString(regionModelList));
        if (CollectionUtils.isNotEmpty(regionModelList)){
            regionVOS.addAll(KsBeanUtil.convertList(regionModelList,RegionCopyVO.class));
        }
        return BaseResponse.success(regionVOS);
    }
}
