package com.wanmi.sbc.setting.provider.impl.doorpick;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.doorpick.DoorPickConfigProvider;
import com.wanmi.sbc.setting.api.request.doorpick.DoorPickConfigRequest;
import com.wanmi.sbc.setting.api.response.doorpick.DoorPickConfigPageResponse;
import com.wanmi.sbc.setting.api.response.doorpick.DoorPickConfigResponse;
import com.wanmi.sbc.setting.bean.vo.DoorPickConfigVO;
import com.wanmi.sbc.setting.doorpick.model.root.DoorPickConfig;
import com.wanmi.sbc.setting.doorpick.request.DoorPickConfigQueryRequest;
import com.wanmi.sbc.setting.doorpick.service.DoorPickConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @desc  
 * @author shiy  2023/8/4 16:31
*/
@Validated
@RestController
@Slf4j
public class DoorPickConfigController implements DoorPickConfigProvider {

    @Autowired
    private DoorPickConfigService doorPickConfigService;

    @Override
    public BaseResponse<DoorPickConfigResponse> getDoorPickConfigAllData(String province) {
        /*List<DoorPickConfig> needQuryData = doorPickConfigRepository.getAllNeedQuryData(province);
        return BaseResponse.success(DoorPickConfigResponse.builder().doorPickConfigVOS(KsBeanUtil.convert(needQuryData, DoorPickConfigVO.class)).build());*/
        return null;
    }


    /**
     * @desc  新增自提点
     * @author shiy  2023/8/12 15:30
    */
    @Override
    public BaseResponse add(DoorPickConfigVO doorPickConfigVO) {
        //List<Long> openedIds = doorPickConfigService.getOpendFlagNetWorkId(doorPickConfigVO.getStoreId());
        doorPickConfigVO.setDelFlag(2);
        doorPickConfigService.add(KsBeanUtil.convert(doorPickConfigVO,DoorPickConfig.class));
        return BaseResponse.SUCCESSFUL();
    }

    /***
     * @desc  修改上门自提
     * @author shiy  2023/8/12 15:38
    */
    @Override
    public BaseResponse modify(DoorPickConfigVO doorPickConfigVO) {
        doorPickConfigService.saveConfig(KsBeanUtil.convert(doorPickConfigVO,DoorPickConfig.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @desc  删除自提点
     * @author shiy  2023/8/12 15:30
    */
    @Override
    public BaseResponse deleteDoorPickConfig(List<Long> list) {
        doorPickConfigService.deleteDoorPickConfigByNetworkIds(list);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @desc  删除自提点
     * @author shiy  2023/8/12 15:30
     */
    @Override
    public BaseResponse stopDoorPickConfig(List<Long> list) {
        doorPickConfigService.stopDoorPickConfigByNetworkIds(list);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @desc  启用自提点
     * @author shiy  2023/8/12 15:31
    */
    @Override
    public BaseResponse startDoorPickConfig(DoorPickConfigRequest doorPickConfigRequest) {
        doorPickConfigService.startDoorPickConfigByNetworkIds(DoorPickConfigQueryRequest.builder().storeId(doorPickConfigRequest.getStoreId()).networkIds(doorPickConfigRequest.getNetworkIds()).build());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<DoorPickConfigResponse> findById(Long networkId) {
        Optional<DoorPickConfig> byId = doorPickConfigService.findById(networkId);
        if (Objects.isNull(byId.get())){
            throw new SbcRuntimeException("未查询到数据");
        }
        return BaseResponse.success(DoorPickConfigResponse.builder().doorPickConfigVOS(KsBeanUtil.convert(Arrays.asList(byId.get()),DoorPickConfigVO.class)).build());
    }

    @Override
    public BaseResponse<DoorPickConfigVO> findOneById(Long networkId) {
        Optional<DoorPickConfig> byId = doorPickConfigService.findById(networkId);
        if (Objects.isNull(byId.get())){
            throw new SbcRuntimeException("未查询到数据");
        }
        DoorPickConfigVO doorPickConfigVO = KsBeanUtil.convert(byId.get(),DoorPickConfigVO.class);
        return BaseResponse.success(doorPickConfigVO);
    }

    /**
     * @desc 根据商家获取启用的自提点
     * @author shiy  2023/8/12 15:33
    */
    @Override
    public BaseResponse<DoorPickConfigVO> findStartedInfoByStoreId(Long storeId) {
        List<DoorPickConfig> doorPickConfigs = doorPickConfigService.qureyDoorPickConfigInfo(DoorPickConfigQueryRequest.builder().storeId(storeId).delFlag(0).build());
        if(CollectionUtils.isEmpty(doorPickConfigs)){
            return BaseResponse.SUCCESSFUL();
        }else{
            DoorPickConfigVO retVo =  KsBeanUtil.convert(doorPickConfigs.get(0), DoorPickConfigVO.class);
            return BaseResponse.success(retVo);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public BaseResponse<DoorPickConfigResponse> listDoorPickConfigResponse(DoorPickConfigRequest doorPickConfigRequest) {
        List<DoorPickConfig> doorPickConfigs = doorPickConfigService.qureyDoorPickConfigInfo
                (KsBeanUtil.convert(doorPickConfigRequest, DoorPickConfigQueryRequest.class));
        return BaseResponse.success(DoorPickConfigResponse.builder().doorPickConfigVOS(KsBeanUtil.convert(doorPickConfigs,DoorPickConfigVO.class)).build());
    }

    @Override
    public BaseResponse<DoorPickConfigPageResponse> pageDoorPickConfigResponse(DoorPickConfigRequest doorPickConfigRequest) {
        DoorPickConfigQueryRequest doorPickConfigQueryRequest = new DoorPickConfigQueryRequest();
        KsBeanUtil.copyPropertiesThird(doorPickConfigRequest, doorPickConfigQueryRequest);
        log.info("网点查询复制数据"+ JSON.toJSONString(doorPickConfigQueryRequest));
        Page<DoorPickConfig> page = doorPickConfigService.findAll(doorPickConfigQueryRequest);
        List<DoorPickConfig> content = page.getContent();
        List<DoorPickConfigVO> convert = KsBeanUtil.convert(content, DoorPickConfigVO.class);
        DoorPickConfigPageResponse response = DoorPickConfigPageResponse.builder()
                .doorPickConfigVOMicroServicePage(new MicroServicePage<>(convert,doorPickConfigRequest.getPageable(),page.getTotalElements()))
                .build();
        return BaseResponse.success(response);
    }
}
