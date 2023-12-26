package com.wanmi.sbc.setting.advertising.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.advertising.model.root.*;
import com.wanmi.sbc.setting.advertising.repository.AdvertisingRetailConfigRepository;
import com.wanmi.sbc.setting.advertising.repository.AdvertisingRetailGoodsConfigRepository;
import com.wanmi.sbc.setting.advertising.repository.AdvertisingRetailRepository;
import com.wanmi.sbc.setting.advertising.request.AdvertisingRetailSaveRequest;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingRetailQueryRequest;
import com.wanmi.sbc.setting.bean.enums.AdvertisingType;
import com.wanmi.sbc.setting.bean.vo.AdvertisingRetailConfigVO;
import com.wanmi.sbc.setting.bean.vo.AdvertisingRetailGoodsConfigVO;
import com.wanmi.sbc.setting.bean.vo.AdvertisingRetailVO;
import com.wanmi.sbc.setting.redis.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description: 散批广告位业务逻辑service
 * @author: XinJiang
 * @time: 2022/4/18 17:45
 */
@Service
public class AdvertisingRetailService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private AdvertisingRetailRepository advertisingRetailRepository;

    @Autowired
    private AdvertisingRetailConfigRepository advertisingRetailConfigRepository;

    @Autowired
    private AdvertisingRetailGoodsConfigRepository advertisingRetailGoodsConfigRepository;

    /**
     * 新增散批广告位信息
     * @param request
     * @throws SbcRuntimeException
     */
    @Transactional(rollbackFor = Exception.class)
    public void addAdvertisingRetail(AdvertisingRetailSaveRequest request) throws SbcRuntimeException{
        AdvertisingRetail advertisingRetail = request.generateAdvertisingRetail();
        advertisingRetail = advertisingRetailRepository.saveAndFlush(advertisingRetail);

        this.saveAdvertisingRetailConfig(request.generateAdvertisingRetailConfig(advertisingRetail.getAdvertisingId()));
        //生成缓存
//        this.fillRedis();
    }

    /**
     * 修改散批广告位信息
     * @param request
     * @throws SbcRuntimeException
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyAdvertisingRetail(AdvertisingRetailSaveRequest request) throws SbcRuntimeException{
        AdvertisingRetail advertisingRetail = advertisingRetailRepository.findById(request.getAdvertisingId())
                .orElseThrow(()-> new SbcRuntimeException(CommonErrorCode.SPECIFIED,"散批广告位信息不存在！"));
        advertisingRetail.setAdvertisingName(request.getAdvertisingName());
        advertisingRetail.setAdvertisingType(request.getAdvertisingType());
        advertisingRetail.setSortNum(request.getSortNum());
        advertisingRetail.setUpdateTime(request.getUpdateTime());
        advertisingRetail.setUpdatePerson(request.getUpdatePerson());

        advertisingRetail = advertisingRetailRepository.saveAndFlush(advertisingRetail);
        if (CollectionUtils.isNotEmpty(advertisingRetail.getAdvertisingRetailConfigs())
                && advertisingRetail.getAdvertisingType().equals(AdvertisingType.COLUMNS)) {
            List<String> configIds = advertisingRetail.getAdvertisingRetailConfigs()
                    .stream().map(AdvertisingRetailConfig::getAdvertisingConfigId).collect(Collectors.toList());
            advertisingRetailGoodsConfigRepository.deleteByConfigIds(configIds);
        }
        advertisingRetailConfigRepository.deleteByAdvertisingId(advertisingRetail.getAdvertisingId());

        this.saveAdvertisingRetailConfig(request.generateAdvertisingRetailConfig(advertisingRetail.getAdvertisingId()));
        //生成缓存
//        this.fillRedis();
    }

    /**
     * 保存散批广告位配置信息
     * @param advertisingRetailConfigs
     */
    private void saveAdvertisingRetailConfig(List<AdvertisingRetailConfig> advertisingRetailConfigs) {
        if (CollectionUtils.isNotEmpty(advertisingRetailConfigs)) {
            advertisingRetailConfigs.forEach(advertisingRetailConfig -> {
                AdvertisingRetailConfig saveConfig = advertisingRetailConfigRepository.saveAndFlush(advertisingRetailConfig);
                if (CollectionUtils.isNotEmpty(advertisingRetailConfig.getAdvertisingRetailGoodsConfigs())) {
                    this.saveAdvertisingRetailGoodsConfig(advertisingRetailConfig.generateAdvertisingRetailGoodsConfig(saveConfig.getAdvertisingConfigId()));
                }
            });
        } else {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
    }

    /**
     * 保存散批分栏商品配置信息
     * @param advertisingRetailGoodsConfigs
     */
    private void saveAdvertisingRetailGoodsConfig(List<AdvertisingRetailGoodsConfig> advertisingRetailGoodsConfigs) {
        if (CollectionUtils.isNotEmpty(advertisingRetailGoodsConfigs)) {
            advertisingRetailGoodsConfigRepository.saveAll(advertisingRetailGoodsConfigs);
        } else {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
    }

    /**
     * 修改启动页广告配置信息状态
     * @param advertisingId
     * @param status
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyStatus(String advertisingId, DefaultFlag status, AdvertisingType type) {
        if (DefaultFlag.YES.equals(status)) {
            advertisingRetailRepository.modifyAllStatusByType(type);
            advertisingRetailRepository.modifyStatusById(advertisingId,status);
        } else {
            advertisingRetailRepository.modifyStatusById(advertisingId,status);
        }
//        this.fillRedis();
    }

    /**
     * 通过id删除广告位信息
     * @param advertisingId
     */
    @Transactional(rollbackFor = Exception.class)
    public void delById(String advertisingId,String delPerson) {
        AdvertisingRetail advertisingRetail = this.findById(advertisingId);
        if (Objects.isNull(advertisingRetail)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"散批广告位信息不存在");
        }
        advertisingRetail.setDelFlag(DeleteFlag.YES);
        advertisingRetail.setDelPerson(delPerson);
        advertisingRetail.setDelTime(LocalDateTime.now());
        advertisingRetailRepository.saveAndFlush(advertisingRetail);
        //生成缓存
//        this.fillRedis();
    }

    /**
     * 通过id获取首页广告位信息
     * @param advertisingId
     * @return
     */
    public AdvertisingRetail findById(String advertisingId) {
        return advertisingRetailRepository.findById(advertisingId).orElse(null);
    }

    /**
     * 分页查询首页广告位信息
     * @param request
     * @return
     */
    public Page<AdvertisingRetail> advertisingPage(AdvertisingRetailQueryRequest request) {
        return advertisingRetailRepository.findAll(AdvertisingWhereCriteriaBuilder.retailBuild(request),request.getPageable());
    }

    /**
     * 获取首页广告信息列表并排序
     * @param request
     * @return
     */
    public List<AdvertisingRetail> advertisingRetails(AdvertisingRetailQueryRequest request) {
        if (Objects.nonNull(request.getSort())) {
            return advertisingRetailRepository.findAll(AdvertisingWhereCriteriaBuilder.retailBuild(request),request.getSort());
        } else {
            return advertisingRetailRepository.findAll(AdvertisingWhereCriteriaBuilder.retailBuild(request));
        }
    }

    /**
     * 获取广告位列表（缓存级）
     * @return
     */
    public List<AdvertisingRetailVO> getAdvertisingListByCache(){
        if (redisService.hasKey(CacheKeyConstant.ADVERTISING_RETAIL_LIST_KEY)) {
            return redisService.getList(CacheKeyConstant.ADVERTISING_RETAIL_LIST_KEY,AdvertisingRetailVO.class);
        }
        //生成缓存
        this.fillRedis();
        return redisService.getList(CacheKeyConstant.ADVERTISING_RETAIL_LIST_KEY,AdvertisingRetailVO.class);
    }

    /**
     * 生成JSON字符串缓存Redis
     */
    public void fillRedis() {
        AdvertisingRetailQueryRequest request = new AdvertisingRetailQueryRequest();
        request.setDelFlag(DeleteFlag.NO);
        request.setStatus(DefaultFlag.YES);
        request.putSort("sortNum", SortType.ASC.toValue());
        List<AdvertisingRetailVO> advertisingRetailVOS = KsBeanUtil.convert(this.advertisingRetails(request),AdvertisingRetailVO.class);

        if (CollectionUtils.isNotEmpty(advertisingRetailVOS)) {
            advertisingRetailVOS.forEach(advertisingRetailVO -> {
                if (AdvertisingType.COLUMNS.equals(advertisingRetailVO.getAdvertisingType())) {

                    List<AdvertisingRetailConfigVO> advertisingRetailConfigs = KsBeanUtil.convert(
                            advertisingRetailConfigRepository.findAdvertisingRetailConfigByAdvertisingId(advertisingRetailVO.getAdvertisingId())
                            , AdvertisingRetailConfigVO.class);

                    advertisingRetailConfigs.forEach(config -> config.setSkuIds(config.getAdvertisingRetailGoodsConfigs()
                            .stream().map(AdvertisingRetailGoodsConfigVO::getGoodsInfoId).collect(Collectors.toList())));

                    advertisingRetailVO.setAdvertisingRetailConfigs(advertisingRetailConfigs);
                }
            });

            redisService.setString(CacheKeyConstant.ADVERTISING_RETAIL_LIST_KEY, JSON.toJSONString(advertisingRetailVOS,
                    SerializerFeature.DisableCircularReferenceDetect),60*60);
        } else {
            redisService.delete(CacheKeyConstant.ADVERTISING_RETAIL_LIST_KEY);
        }
    }
}
