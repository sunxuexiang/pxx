package com.wanmi.sbc.setting.advertising.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.advertising.model.root.Advertising;
import com.wanmi.sbc.setting.advertising.model.root.AdvertisingConfig;
import com.wanmi.sbc.setting.advertising.model.root.AdvertisingWhereCriteriaBuilder;
import com.wanmi.sbc.setting.advertising.model.root.StartPageAdvertising;
import com.wanmi.sbc.setting.advertising.repository.AdvertisingConfigRepository;
import com.wanmi.sbc.setting.advertising.repository.AdvertisingRepository;
import com.wanmi.sbc.setting.advertising.repository.StartPageAdvertisingRepository;
import com.wanmi.sbc.setting.advertising.request.AdvertisingSaveRequest;
import com.wanmi.sbc.setting.advertising.request.StartPageAdvertisingSaveRequest;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingQueryRequest;
import com.wanmi.sbc.setting.api.request.advertising.StartPageAdvertisingQueryRequest;
import com.wanmi.sbc.setting.bean.vo.AdvertisingVO;
import com.wanmi.sbc.setting.bean.vo.StartPageAdvertisingVO;
import com.wanmi.sbc.setting.redis.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @description: 首页广告位业务逻辑
 * @author: XinJiang
 * @time: 2022/2/18 10:22
 */
@Service
public class AdvertisingService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private AdvertisingRepository advertisingRepository;

    @Autowired
    private AdvertisingConfigRepository advertisingConfigRepository;

    @Autowired
    private StartPageAdvertisingRepository startPageAdvertisingRepository;

    /**
     * 广告位信息商家端列表缓存key
     */
    private final static String ADVERTISING_LIST_STORE_ID_KEY = "ADVERTISING_LIST_STORE_ID_KEY";

    /**
     * 新增首页广告位信息
     * @param request
     * @throws SbcRuntimeException
     */
    @Transactional(rollbackFor = Exception.class)
    public void addAdvertising(AdvertisingSaveRequest request) throws SbcRuntimeException{
        Advertising advertising = request.generateAdvertising();
        advertising = advertisingRepository.saveAndFlush(advertising);

        this.saveAdvertisingConfig(request.generateAdvertisingConfig(advertising.getAdvertisingId()));
        //生成缓存 如果是运营端 使用仓库ID wareId缓存 商家端使用店铺ID storeId生成缓存
        if(Objects.isNull(request.getWareId())){
            this.fillStoreIdRedis(request.getStoreId());
        } else {
            this.fillRedis(request.getWareId());
        }
    }

    /**
     * 修改首页广告位信息
     * @param request
     * @throws SbcRuntimeException
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyAdvertising(AdvertisingSaveRequest request) throws SbcRuntimeException{
        Advertising advertising = advertisingRepository.findById(request.getAdvertisingId())
                .orElseThrow(()-> new SbcRuntimeException(CommonErrorCode.SPECIFIED,"首页广告位信息不存在！"));
        advertising.setAdvertisingName(request.getAdvertisingName());
        advertising.setAdvertisingType(request.getAdvertisingType());
        advertising.setSortNum(request.getSortNum());
        advertising.setUpdateTime(request.getUpdateTime());
        advertising.setUpdatePerson(request.getUpdatePerson());
        advertising.setWareId(request.getWareId());

        advertising = advertisingRepository.saveAndFlush(advertising);

        advertisingConfigRepository.deleteByAdvertisingId(advertising.getAdvertisingId());

        this.saveAdvertisingConfig(request.generateAdvertisingConfig(advertising.getAdvertisingId()));
        //生成缓存 如果是运营端 使用仓库ID wareId缓存 商家端使用店铺ID storeId生成缓存
        if(Objects.isNull(request.getWareId())){
            this.fillStoreIdRedis(request.getStoreId());
        } else {
            this.fillRedis(request.getWareId());
        }
    }

    /**
     * 保存首页广告位配置信息
     * @param advertisingConfigList
     */
    private void saveAdvertisingConfig(List<AdvertisingConfig> advertisingConfigList) {
        if (CollectionUtils.isNotEmpty(advertisingConfigList)) {
            advertisingConfigList.forEach(advertisingConfig -> {
                advertisingConfigRepository.saveAndFlush(advertisingConfig);
            });
        } else {
            throw new SbcRuntimeException("K-000009");
        }
    }

    /**
     * 通过id删除广告位信息
     * @param advertisingId
     */
    @Transactional(rollbackFor = Exception.class)
    public void delById(String advertisingId,String delPerson) {
        Advertising advertising = this.findById(advertisingId);
        if (Objects.isNull(advertising)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"广告位信息不存在");
        }
        advertising.setDelFlag(DeleteFlag.YES);
        advertising.setDelPerson(delPerson);
        advertising.setDelTime(LocalDateTime.now());
        advertisingRepository.saveAndFlush(advertising);
        //生成缓存 如果是运营端 使用仓库ID wareId缓存 商家端使用店铺ID storeId生成缓存
        if(Objects.isNull(advertising.getWareId())){
            this.fillStoreIdRedis(advertising.getStoreId());
        } else {
            this.fillRedis(advertising.getWareId());
        }
    }

    /**
     * 通过id获取首页广告位信息
     * @param advertisingId
     * @return
     */
    public Advertising findById(String advertisingId) {
        return advertisingRepository.findById(advertisingId).orElse(null);
    }

    /**
     * 分页查询首页广告位信息
     * @param request
     * @return
     */
    public Page<Advertising> advertisingPage(AdvertisingQueryRequest request) {
        return advertisingRepository.findAll(AdvertisingWhereCriteriaBuilder.build(request),request.getPageable());
    }

    /**
     * 分页查询商家端首页广告位信息
     * @param request
     * @return
     */
    public Page<Advertising> advertisingStorePage(AdvertisingQueryRequest request) {
        request.setSortColumn("storeId");
        request.setSortRole("desc");
        return advertisingRepository.findAll(AdvertisingWhereCriteriaBuilder.buildStore(request),request.getPageRequest());
    }

    /**
     * 获取首页广告信息列表并排序
     * @param request
     * @return
     */
    public List<Advertising> advertisingList(AdvertisingQueryRequest request) {
        List<Advertising> advertisingList = Lists.newArrayList();
        if (Objects.nonNull(request.getSort())) {
            advertisingList = advertisingRepository.findAll(AdvertisingWhereCriteriaBuilder.build(request),request.getSort());
        } else {
            advertisingList = advertisingRepository.findAll(AdvertisingWhereCriteriaBuilder.build(request));
        }
        //遍历赋值配置信息
        if(CollectionUtils.isNotEmpty(advertisingList)) {
            advertisingList.forEach( advertising -> {
                advertising.setAdvertisingConfigList(advertisingConfigList(advertising.getAdvertisingId()));
            });
        }
        return advertisingList;
    }

    /**
     * 通过首页广告位id查询广告位配置信息集合
     * @param advertisingId
     * @return
     */
    public List<AdvertisingConfig> advertisingConfigList(String advertisingId){
       return advertisingConfigRepository.getAdvertisingConfigByAdvertisingId(advertisingId);
    }

    /**
     * 获取广告位列表（缓存级）
     * @return
     */
    public List<AdvertisingVO> getAdvertisingListByCache(Long wareId){
        if (redisService.hasKey(CacheKeyConstant.ADVERTISING_LIST_KEY.concat(wareId.toString()))) {
            return redisService.getList(CacheKeyConstant.ADVERTISING_LIST_KEY.concat(wareId.toString()),AdvertisingVO.class);
        }
        //生成缓存
        this.fillRedis(wareId);
        return redisService.getList(CacheKeyConstant.ADVERTISING_LIST_KEY.concat(wareId.toString()),AdvertisingVO.class);
    }

    /**
     * 获取商家端广告位列表（缓存级）
     * @return
     */
    public List<AdvertisingVO> getAdvertisingListStoreIdByCache(Long storeId){
        if (redisService.hasKey(ADVERTISING_LIST_STORE_ID_KEY.concat(storeId.toString()))) {
            return redisService.getList(ADVERTISING_LIST_STORE_ID_KEY.concat(storeId.toString()),AdvertisingVO.class);
        }
        //生成缓存
        this.fillStoreIdRedis(storeId);
        return redisService.getList(ADVERTISING_LIST_STORE_ID_KEY.concat(storeId.toString()),AdvertisingVO.class);
    }

    /**
     * 生成JSON字符串缓存Redis
     */
    public void fillRedis(Long wareId) {
        AdvertisingQueryRequest request = new AdvertisingQueryRequest();
        request.setWareId(wareId);
        request.setDelFlag(DeleteFlag.NO);
        request.putSort("sortNum", SortType.ASC.toValue());
        List<AdvertisingVO> advertisingVOList = KsBeanUtil.convertList(advertisingList(request),AdvertisingVO.class);
        redisService.setString(CacheKeyConstant.ADVERTISING_LIST_KEY.concat(wareId.toString()), JSON.toJSONString(advertisingVOList,
                SerializerFeature.DisableCircularReferenceDetect),60*60);
    }

    /**
     * 生成JSON字符串缓存Redis
     */
    public void fillStoreIdRedis(Long storeId) {
        AdvertisingQueryRequest request = new AdvertisingQueryRequest();
        request.setStoreId(storeId);
        request.setDelFlag(DeleteFlag.NO);
        request.putSort("sortNum", SortType.ASC.toValue());
        List<AdvertisingVO> advertisingVOList = KsBeanUtil.convertList(advertisingList(request),AdvertisingVO.class);
        redisService.setString(ADVERTISING_LIST_STORE_ID_KEY.concat(storeId.toString()), JSON.toJSONString(advertisingVOList,
                SerializerFeature.DisableCircularReferenceDetect),60*60);
    }


    /**
     * 新增启动页广告位信息
     * @param request
     * @throws SbcRuntimeException
     */
    @Transactional(rollbackFor = Exception.class)
    public void addStartPageAdvertising(StartPageAdvertisingSaveRequest request) throws SbcRuntimeException{
        StartPageAdvertising advertising = request.generateStartPageAdvertising();
        advertising = startPageAdvertisingRepository.saveAndFlush(advertising);

        //生成缓存
        this.fillStartPageRedis();
    }

    /**
     * 修改启动页广告位信息
     * @param request
     * @throws SbcRuntimeException
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyStartPageAdvertising(StartPageAdvertisingSaveRequest request) throws SbcRuntimeException{
        StartPageAdvertising advertising = startPageAdvertisingRepository.findById(request.getAdvertisingId())
                .orElseThrow(()-> new SbcRuntimeException(CommonErrorCode.SPECIFIED,"启动页页广告位信息不存在！"));
        advertising.setAdvertisingName(request.getAdvertisingName());
        advertising.setBackgroundColor(request.getBackgroundColor());
        advertising.setImageUrl(request.getImageUrl());
        advertising.setLinkFlag(request.getLinkFlag());
        advertising.setMofangName(request.getMofangName());
        advertising.setMofangCode(request.getMofangCode());
        advertising.setEffectType(request.getEffectType());
        advertising.setEffectDate(request.getEffectDate());
        advertising.setStatus(request.getStatus());
        advertising.setUpdateTime(request.getUpdateTime());
        advertising.setUpdatePerson(request.getUpdatePerson());

        advertising = startPageAdvertisingRepository.saveAndFlush(advertising);

        //生成缓存
        this.fillStartPageRedis();
    }

    /**
     * 通过id删除启动页广告信息
     * @param advertisingId
     */
    @Transactional(rollbackFor = Exception.class)
    public void delStartPageAdvertisingById(String advertisingId,String delPerson) {
        StartPageAdvertising advertising = this.findStartPageById(advertisingId);
        if (Objects.isNull(advertising)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"启动页广告信息不存在");
        }
        advertising.setDelFlag(DeleteFlag.YES);
        advertising.setDelPerson(delPerson);
        advertising.setDelTime(LocalDateTime.now());
        startPageAdvertisingRepository.saveAndFlush(advertising);
        //生成缓存
        this.fillStartPageRedis();
    }

    /**
     * 修改启动页广告配置信息状态
     * @param advertisingId
     * @param status
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyStartPageStatus(String advertisingId,DefaultFlag status) {
        if (DefaultFlag.YES.equals(status)) {
            startPageAdvertisingRepository.modifyAllStartPageStatus();
            startPageAdvertisingRepository.modifyStartPageStatusById(advertisingId,status);
        } else {
            startPageAdvertisingRepository.modifyStartPageStatusById(advertisingId,status);
        }
        this.fillStartPageRedis();
    }

    /**
     * 通过id获取启动页广告位信息
     * @param advertisingId
     * @return
     */
    public StartPageAdvertising findStartPageById(String advertisingId) {
        return startPageAdvertisingRepository.findById(advertisingId).orElse(null);
    }

    /**
     * 获取启动页广告信息列表
     * @param request
     * @return
     */
    public List<StartPageAdvertising> startPageAdvertisingList(StartPageAdvertisingQueryRequest request) {
        if (Objects.nonNull(request.getSort())) {
            return startPageAdvertisingRepository.findAll(AdvertisingWhereCriteriaBuilder.buildStartPage(request),request.getSort());
        } else {
            return startPageAdvertisingRepository.findAll(AdvertisingWhereCriteriaBuilder.buildStartPage(request));
        }
    }

    /**
     * 分页查询首页广告位信息
     * @param request
     * @return
     */
    public Page<StartPageAdvertising> startPageAdvertisingPage(StartPageAdvertisingQueryRequest request) {
        return startPageAdvertisingRepository.findAll(AdvertisingWhereCriteriaBuilder.buildStartPage(request),request.getPageable());
    }

    /**
     * 生成JSON字符串缓存Redis 启动页广告位
     */
    public void fillStartPageRedis() {
        StartPageAdvertisingQueryRequest request = new StartPageAdvertisingQueryRequest();
        request.setDelFlag(DeleteFlag.NO);
        request.setStatus(DefaultFlag.YES);
        StartPageAdvertising startPageAdvertising = this.startPageAdvertisingList(request).stream().findFirst().orElse(null);
        if (Objects.nonNull(startPageAdvertising)) {
            redisService.setString(CacheKeyConstant.START_PAGE_ADVERTISING,
                    JSON.toJSONString(KsBeanUtil.convert(startPageAdvertising, StartPageAdvertisingVO.class),
                    SerializerFeature.DisableCircularReferenceDetect),60*60);
        } else {
            redisService.delete(CacheKeyConstant.START_PAGE_ADVERTISING);
        }
    }

    /**
     * 获取广告位列表（缓存级）
     * @return
     */
    public StartPageAdvertisingVO getStartPageAdvertisingByCache(){
        if (redisService.hasKey(CacheKeyConstant.START_PAGE_ADVERTISING)) {
            return KsBeanUtil.convert(redisService.getObj(CacheKeyConstant.START_PAGE_ADVERTISING,StartPageAdvertising.class),
                    StartPageAdvertisingVO.class);
        }
        //生成缓存
        this.fillStartPageRedis();
        return KsBeanUtil.convert(redisService.getObj(CacheKeyConstant.START_PAGE_ADVERTISING,StartPageAdvertising.class),
                StartPageAdvertisingVO.class);
    }
}
