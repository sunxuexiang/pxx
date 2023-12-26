package com.wanmi.sbc.setting.hotstylemoments.service;

import com.mysql.cj.log.Log;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.hotstylemoments.HotStyleMomentsCheckTimeRequest;
import com.wanmi.sbc.setting.api.request.hotstylemoments.HotStyleMomentsQueryRequest;
import com.wanmi.sbc.setting.bean.enums.HotStyleMomentsStatus;
import com.wanmi.sbc.setting.bean.vo.HotStyleMomentsVO;
import com.wanmi.sbc.setting.hotstylemoments.model.root.HotStyleMoments;
import com.wanmi.sbc.setting.hotstylemoments.model.root.HotStyleMomentsConfig;
import com.wanmi.sbc.setting.hotstylemoments.model.root.HotStyleMomentsWhereCriteriaBuilder;
import com.wanmi.sbc.setting.hotstylemoments.repository.HotStyleMomentsConfigRepository;
import com.wanmi.sbc.setting.hotstylemoments.repository.HotStyleMomentsRepository;
import com.wanmi.sbc.setting.hotstylemoments.request.HotStyleMomentsSaveRequest;
import com.wanmi.sbc.setting.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @description: 爆款时刻service
 * @author: XinJiang
 * @time: 2022/5/9 18:41
 */
@Service
@Slf4j
public class HotStyleMomentsService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private HotStyleMomentsRepository hotStyleMomentsRepository;

    @Autowired
    private HotStyleMomentsConfigRepository hotStyleMomentsConfigRepository;

    /**
     * 新增爆款时刻
     * @param request
     */
    @Transactional(rollbackFor = Exception.class)
    public void addHotStyleMoments(HotStyleMomentsSaveRequest request) {
        HotStyleMoments hotStyleMoments = request.generateHotStyleMoments();
        hotStyleMoments = hotStyleMomentsRepository.saveAndFlush(hotStyleMoments);
        this.saveHotStyleMomentsConfigs(request.generateHotStyleMomentsConfigs(hotStyleMoments.getHotId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void modifyHotStyleMoments(HotStyleMomentsSaveRequest request) {
        HotStyleMoments hotStyleMoments = this.getById(request.getHotId());
        if (Objects.nonNull(hotStyleMoments)) {
            hotStyleMoments.setHotName(request.getHotName());
            hotStyleMoments.setBeginTime(request.getBeginTime());
            hotStyleMoments.setEndTime(request.getEndTime());
            hotStyleMoments.setBannerImageUrl(request.getBannerImageUrl());

            hotStyleMoments.setUpdateTime(request.getUpdateTime());
            hotStyleMoments.setUpdatePerson(request.getUpdatePerson());

            hotStyleMomentsConfigRepository.deleteByHotId(hotStyleMoments.getHotId());
            this.saveHotStyleMomentsConfigs(request.generateHotStyleMomentsConfigs(hotStyleMoments.getHotId()));
        } else {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"爆款时刻信息不存在！");
        }
    }

    /**
     * 保存爆款时刻配置信息
     * @param hotStyleMomentsConfigList
     */
    public void saveHotStyleMomentsConfigs(List<HotStyleMomentsConfig> hotStyleMomentsConfigList) {
        if (CollectionUtils.isNotEmpty(hotStyleMomentsConfigList)) {
            hotStyleMomentsConfigRepository.saveAll(hotStyleMomentsConfigList);
        } else {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
    }

    /**
     * 终止活动
     * @param hotId
     */
    @Transactional(rollbackFor = Exception.class)
    public void terminationHotStyleMoments(Long hotId) {
        hotStyleMomentsRepository.terminationHotStyleMoments(hotId);
    }

    /**
     * 启动或暂停爆款时刻活动
     * @param hotId
     * @param pauseFlag
     */
    @Transactional(rollbackFor = Exception.class)
    public void pauseHotStyleMoments(Long hotId, DefaultFlag pauseFlag) {
        hotStyleMomentsRepository.pauseHotStyleMoments(pauseFlag,hotId);
    }

    /**
     * 提前开始活动
     * @param hotId
     */
    @Transactional(rollbackFor = Exception.class)
    public void earlyStart(Long hotId) {
        //查询进行中的活动
        List<HotStyleMoments> hotStyleMomentsList = this.getList(HotStyleMomentsQueryRequest.builder()
                .delFlag(DeleteFlag.NO)
                .status(HotStyleMomentsStatus.STARTED).build());
        //终止进行中的活动
        if (CollectionUtils.isNotEmpty(hotStyleMomentsList)) {
            hotStyleMomentsList.forEach(hotStyleMoments -> hotStyleMomentsRepository.terminationHotStyleMoments(hotStyleMoments.getHotId()));
        }
        //提前开始活动
        hotStyleMomentsRepository.earlyStart(DateUtil.parse(DateUtil.nowTime(),DateUtil.FMT_TIME_1),hotId);
    }

    /**
     * 批量删除爆款时刻信息
     * @param hotIds
     */
    public void delByIds(String deletePerson, LocalDateTime deleteTime, List<Long> hotIds) {
        hotIds.forEach(hotId -> this.delById(deletePerson, deleteTime, hotId));
    }

    /**
     * 根据id删除爆款时刻信息（逻辑删除）
     * @param deletePerson
     * @param deleteTime
     * @param hotId
     */
    @Transactional(rollbackFor = Exception.class)
    public void delById(String deletePerson, LocalDateTime deleteTime, Long hotId) {
        hotStyleMomentsRepository.delById(deletePerson, deleteTime, hotId);
    }

    /**
     * 根据id获取爆款时刻信息
     * @param hotId
     * @return
     */
    public HotStyleMoments getById(Long hotId) {
        return hotStyleMomentsRepository.findById(hotId).orElse(null);
    }

    /**
     * 条件查询爆款时刻列表信息
     * @param request
     * @return
     */
    public List<HotStyleMoments> getList(HotStyleMomentsQueryRequest request) {
        if (Objects.nonNull(request.getSort())) {
            return hotStyleMomentsRepository.findAll(HotStyleMomentsWhereCriteriaBuilder.build(request), request.getSort());
        } else {
            return hotStyleMomentsRepository.findAll(HotStyleMomentsWhereCriteriaBuilder.build(request));
        }
    }

    /**
     * 分页查询爆款时刻信息
     * @param request
     * @return
     */
    public Page<HotStyleMoments> getPage(HotStyleMomentsQueryRequest request) {
        return hotStyleMomentsRepository.findAll(HotStyleMomentsWhereCriteriaBuilder.build(request), request.getPageable());
    }

    /**
     * 生成redis缓存
     */
    public void fillRedis(){
        HotStyleMomentsQueryRequest request = new HotStyleMomentsQueryRequest();
        request.setDelFlag(DeleteFlag.NO);
        request.setTerminationFlag(DefaultFlag.NO);
        request.setStatus(HotStyleMomentsStatus.STARTED);
        request.putSort("createTime", SortType.DESC.toValue());
        List<HotStyleMoments> hotStyleMomentsList = this.getList(request);
        //如果爆款时刻信息为空 查询最近结束的一场活动并缓存
        if (CollectionUtils.isEmpty(hotStyleMomentsList)) {
            request.setStatus(HotStyleMomentsStatus.ENDED);
            hotStyleMomentsList = getList(request);
            hotStyleMomentsList.sort(Comparator.comparing(HotStyleMoments::getEndTime).reversed());
        }
        HotStyleMoments hotStyleMoments = hotStyleMomentsList.stream().findFirst().orElse(null);
        redisService.setObj(CacheKeyConstant.RETAIL_HOT_STYLE_MOMENTS, KsBeanUtil.convert(hotStyleMoments, HotStyleMomentsVO.class),120);
    }

    /**
     * 校验活动时间是否重复
     * @param request
     * @return
     */
    public List<HotStyleMoments> checkTime(HotStyleMomentsCheckTimeRequest request) {
        if (Objects.nonNull(request.getHotId())) {
            return hotStyleMomentsRepository.checkTimeNotSelf(request.getBeginTime(), request.getHotId());
        } else {
            return hotStyleMomentsRepository.checkTime(request.getBeginTime());
        }
    }
}
