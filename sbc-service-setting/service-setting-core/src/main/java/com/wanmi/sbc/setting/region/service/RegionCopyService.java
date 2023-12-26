package com.wanmi.sbc.setting.region.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.bean.vo.RegionCopyFrontVO;
import com.wanmi.sbc.setting.bean.vo.RegionVO;
import com.wanmi.sbc.setting.region.model.RegionCopy;
import com.wanmi.sbc.setting.region.model.RegionModel;
import com.wanmi.sbc.setting.region.repository.RegionCopyRepository;
import com.wanmi.sbc.setting.region.repository.RegionQueryRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Service("RegionCopyService")
@Slf4j
public class RegionCopyService {
    @Autowired
    private RegionCopyRepository regionCopyRepository;

    @Transactional
    public BaseResponse addList(List<RegionCopy> regionList){
        if(CollectionUtils.isEmpty(regionList)){
            return BaseResponse.SUCCESSFUL();
        }
        regionCopyRepository.saveAll(regionList);

        return BaseResponse.SUCCESSFUL();
    }

    public BaseResponse queryRegionByCode(Long code){
        return BaseResponse.success(regionCopyRepository.queryRegionByCode(code));
    }

    public List<RegionCopyFrontVO> getFrontJsonByLevel(Integer level){
        List<RegionCopy> regionCopyList = regionCopyRepository.queryRegionByLevel(level);
        List<RegionCopyFrontVO> regionCopyFrontVOList = new ArrayList<>(regionCopyList.size());
        if(CollectionUtils.isNotEmpty(regionCopyList)){
            for(RegionCopy regionCopy:regionCopyList){
                RegionCopyFrontVO regionCopyFrontVO = new RegionCopyFrontVO();
                regionCopyFrontVO.setCode(Objects.toString(regionCopy.getCode()));
                regionCopyFrontVO.setName(regionCopy.getName());
                if(regionCopy.getParentCode()>0) {
                    regionCopyFrontVO.setParent_code(Objects.toString(regionCopy.getParentCode()));
                }
            }
        }
        return regionCopyFrontVOList;
    }
}
