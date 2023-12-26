package com.wanmi.sbc.setting.doorpick.service;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.setting.doorpick.model.root.DoorPickConfig;
import com.wanmi.sbc.setting.doorpick.repository.DoorPickConfigRepository;
import com.wanmi.sbc.setting.doorpick.request.DoorPickConfigQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 上门自提服务接口
 * Created by CHENLI on 2017/4/19.
 */
@Service
@Transactional
@Slf4j
public class DoorPickConfigService {

    @Autowired
    private DoorPickConfigRepository doorPickConfigRepository;

    public void add(DoorPickConfig doorPickConfig){
        doorPickConfig.setCreateTime(LocalDateTime.now());
        saveConfig(doorPickConfig);
    }

    public void saveConfig(DoorPickConfig doorPickConfig) {
        List<Long> netWorkIdList = getOpendFlagNetWorkId(doorPickConfig.getStoreId());
        if(doorPickConfig.getDelFlag()==0 && CollectionUtils.isNotEmpty(netWorkIdList)) {
            if (doorPickConfig.getNetworkId() == null) {
                throw new SbcRuntimeException("当前店铺已经存在1个开启的自提点");
            } else {
                if (!netWorkIdList.contains(doorPickConfig.getNetworkId())) {
                    throw new SbcRuntimeException("当前店铺已经存在1个开启的自提点");
                }
            }
        }
        doorPickConfigRepository.saveAndFlush(doorPickConfig);
    }
    /***
     * @desc 获取网点下启用的自提点
     * @author shiy  2023/8/12 15:26
    */
    public List<Long> getOpendFlagNetWorkId(Long storeId){
        return doorPickConfigRepository.getOpenNetWorkIdByStoreId(storeId);
    }

    /**
     * 通过动态条件查询网点信息（用于运营后台查询）
     * @param doorPickConfigQueryRequest
     * @return
     */
    public List<DoorPickConfig> qureyDoorPickConfigInfo(DoorPickConfigQueryRequest doorPickConfigQueryRequest){
        List<DoorPickConfig> all = doorPickConfigRepository.findAll(doorPickConfigQueryRequest.getWhereCriteria());
        return all;
    }

    /**
     * 查询Page
     * @param doorPickConfigQueryRequest
     * @return
     */
    public Page<DoorPickConfig> findAll(DoorPickConfigQueryRequest doorPickConfigQueryRequest){
        log.info("查询条件"+JSON.toJSONString(doorPickConfigQueryRequest.getWhereCriteria()));
        log.info("查询分页条件"+JSON.toJSONString(doorPickConfigQueryRequest.getPageRequest()));
        Page<DoorPickConfig> all = doorPickConfigRepository.findAll(doorPickConfigQueryRequest.getWhereCriteria(), doorPickConfigQueryRequest.getPageRequest());
        return all;
    }


    /**
     * 批量删除网点
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDoorPickConfigByNetworkIds (List<Long> list) {
        //停用网点将network表的deletflag改1
        doorPickConfigRepository.deleteDoorPickConfigByNetworkIds(list);
    }

    /**
     * @desc 批量停用网点
     * @author shiy  2023/8/18 9:14
    */
    @Transactional(rollbackFor = Exception.class)
    public void stopDoorPickConfigByNetworkIds (List<Long> list) {
        //停用网点将network表的deletflag改1
        doorPickConfigRepository.stopDoorPickConfigByNetworkIds(list);
    }

    /**
     * 批量启用网点
     */
    @Transactional(rollbackFor = Exception.class)
    public void startDoorPickConfigByNetworkIds (DoorPickConfigQueryRequest doorPickConfigQueryRequest) {
        if(Objects.nonNull(doorPickConfigQueryRequest.getStoreId())) {
            List<DoorPickConfig> doorPickConfigs = qureyDoorPickConfigInfo(DoorPickConfigQueryRequest.builder().storeId(doorPickConfigQueryRequest.getStoreId()).delFlag(0).build());
            if(CollectionUtils.isNotEmpty(doorPickConfigs)){
                stopDoorPickConfigByNetworkIds(Arrays.asList(doorPickConfigs.get(0).getNetworkId()));
            }
        }
        //停用网点将network表的deletflag改0
        doorPickConfigRepository.startDoorPickConfigByNetworkIds(doorPickConfigQueryRequest.getNetworkIds());
    }

    public Optional<DoorPickConfig> findById(Long networkId) {
        return doorPickConfigRepository.findById(networkId);
    }
}
