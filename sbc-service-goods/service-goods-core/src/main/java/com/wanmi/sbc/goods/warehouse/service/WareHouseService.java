package com.wanmi.sbc.goods.warehouse.service;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.constant.WareHouseConstants;
import com.wanmi.sbc.goods.api.constant.WareHouseErrorCode;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseModifyDefaultFlagRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseModifyRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseQueryRequest;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.goods.redis.RedisService;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.repository.WareHouseRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>仓库表业务逻辑</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@Service("WareHouseService")
public class WareHouseService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private WareHouseRepository wareHouseRepository;



    /**
     * 新增仓库表
     *
     * @author zhangwenchang
     */
    @Transactional
    public WareHouse add(WareHouse entity) {
        wareHouseRepository.save(entity);
        saveAndFlushRedise(WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        return entity;
    }

    /**
     * 修改仓库表
     *
     * @author zhangwenchang
     */
    @Transactional
    public WareHouse modify(WareHouse entity) {
        wareHouseRepository.save(entity);
        saveAndFlushRedise(WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        return entity;
    }

    /**
     * 修改仓库为默认仓
     *
     * @author huapeiliang
     */
    @Transactional
    public WareHouse modifyDefaultFlag(WareHouseModifyDefaultFlagRequest modifyReq) {
        wareHouseRepository.cancelDefaultFlagByStoreId(modifyReq.getStoreId());
        WareHouse wareHouse = wareHouseRepository.findByWareIdAndStoreIdAndDelFlag(modifyReq.getWareId(),
                modifyReq.getStoreId(), DeleteFlag.NO)
                .orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "仓库表不存在"));
        wareHouse.setDefaultFlag(DefaultFlag.YES);
        wareHouse.setUpdatePerson(modifyReq.getUpdatePerson());
        wareHouse.setUpdateTime(LocalDateTime.now());
        saveAndFlushRedise(WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        return Optional.ofNullable(wareHouseRepository.save(wareHouse)).orElseGet(WareHouse::new);
    }

    public WareHouse queryWareHouseByStoreIdAndProvinceIdAndCityId(WareHouseQueryRequest wareHouseQueryRequest) {
        WareHouse wareHouse =
                wareHouseRepository.queryWareHouseByStoreIdAndProvinceIdAndCityId(wareHouseQueryRequest.getStoreId(),
                        wareHouseQueryRequest.getProvinceId(), wareHouseQueryRequest.getCityId());
        return wareHouse;
    }

    public WareHouse findByStoreIdAndDefaultFlag(WareHouseQueryRequest wareHouseQueryRequest) {
        WareHouse wareHouse = wareHouseRepository.findByStoreIdAndDefaultFlag(wareHouseQueryRequest.getStoreId(),
                wareHouseQueryRequest.getDefaultFlag());
        return wareHouse;
    }

    public List<WareHouse> findByStoreIdIn(List<Long> wareIds){
       return wareHouseRepository.findByStoreIdIn(wareIds);
    }

    /**
     * 单个删除仓库表
     *
     * @author zhangwenchang
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void deleteById(WareHouse entity) {
        wareHouseRepository.save(entity);
        saveAndFlushRedise(WareHouseConstants.WARE_HOUSE_MAIN_FILED);
    }

    /**
     * 批量删除仓库表
     *
     * @author zhangwenchang
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void deleteByIdList(List<WareHouse> infos) {
        wareHouseRepository.saveAll(infos);
        saveAndFlushRedise(WareHouseConstants.WARE_HOUSE_MAIN_FILED);
    }


    /**
     * 批量删除仓库表
     *
     * @author zhangwenchang
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void deleteByIds(List<Long> infos) {
        wareHouseRepository.deleteByIdList(infos);
        saveAndFlushRedise(WareHouseConstants.WARE_HOUSE_MAIN_FILED);
    }

    /**
     * 单个查询仓库表
     *
     * @author zhangwenchang
     */
    public WareHouse getOne(Long id, Long storeId) {
        return wareHouseRepository.findByWareIdAndStoreIdAndDelFlag(id, storeId, DeleteFlag.NO)
                .orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "仓库表不存在"));
    }

    /**
     * 获取所有散批仓
     */
    public List<WareHouse> getBulkWareHouse(int defaultFlag){
        return wareHouseRepository.queryWareHouseByAndDfAndDefaultFlag(defaultFlag);
    }


    /**
     * 功能描述: 查询仓库信息
     */
    public WareHouse getOneById(Long wareId){
        return  wareHouseRepository.getOne(wareId);
    }


    /**
     * 分页查询仓库表
     *
     * @author zhangwenchang
     */
    public Page<WareHouse> page(WareHouseQueryRequest queryReq) {
        return wareHouseRepository.findAll(
                WareHouseWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 列表查询仓库表
     *
     * @author zhangwenchang
     */
    public List<WareHouse> list(WareHouseQueryRequest queryReq) {
        return wareHouseRepository.findAll(WareHouseWhereCriteriaBuilder.build(queryReq));
    }

    /**
     * 将实体包装成VO
     *
     * @author zhangwenchang
     */
    public WareHouseVO wrapperVo(WareHouse wareHouse) {
        if (wareHouse != null) {
            WareHouseVO wareHouseVO = KsBeanUtil.convert(wareHouse, WareHouseVO.class);
            return wareHouseVO;
        }
        return null;
    }

    public List<Long> getSelectAreas(Long storeId, Long wareId, WareHouseType wareHouseType) {
        List<WareHouse> wareHouseList = wareHouseRepository.findAllByStoreIdAndDelFlagAndWareHouseType(storeId, DeleteFlag.NO,wareHouseType);
        return wareHouseList.stream().filter(info -> !Objects.equals(info.getWareId(), wareId)).map(info -> {
            if (StringUtils.isBlank(info.getDestinationArea())) {
                return null;
            }
            return Arrays.asList(info.getDestinationArea().split(","));
        }).filter(Objects::nonNull).flatMap(Collection::stream).map(Long::valueOf).collect(Collectors.toList());
    }

    /**
     * 校验新增编辑仓库编号和仓库名称是否重复
     *
     * @param wareHouseModifyRequest
     */
    public WareHouse checkParam(WareHouseModifyRequest wareHouseModifyRequest) {
        WareHouse wareHouse = new WareHouse();
        boolean baseExist = Objects.isNull(wareHouseModifyRequest.getWareId());
        if (!baseExist) {
            Optional<WareHouse> wareHouseExist =
                    wareHouseRepository.findByWareIdAndStoreIdAndDelFlag(wareHouseModifyRequest.getWareId(),
                            wareHouseModifyRequest.getStoreId(),
                            DeleteFlag.NO);
            if (!wareHouseExist.isPresent()) {
                //仓库不存在
                throw new SbcRuntimeException(WareHouseErrorCode.WARE_HOUSE_NOT_EXIST,"仓库不存在");
            }
            wareHouse = wareHouseExist.get();
        }

        Optional<WareHouse> wareHouseCode =
                wareHouseRepository.findFirstByStoreIdAndWareCodeAndDelFlag(wareHouseModifyRequest.getStoreId(),
                        wareHouseModifyRequest.getWareCode(), DeleteFlag.NO);

        boolean addCodeExist = baseExist && wareHouseCode.isPresent();
        boolean modifyCodeExist =
                Objects.nonNull(wareHouseModifyRequest.getWareId()) && wareHouseCode.isPresent() && !wareHouseCode.get().getWareId().equals(wareHouseModifyRequest.getWareId());
        if (addCodeExist || modifyCodeExist) {
            //编号重复
            throw new SbcRuntimeException(WareHouseErrorCode.WARE_CODE_EXIST,"编号重复");
        }
        Optional<WareHouse> wareHouseName =
                wareHouseRepository.findFirstByStoreIdAndWareNameAndDelFlag(wareHouseModifyRequest.getStoreId(),
                        wareHouseModifyRequest.getWareName(), DeleteFlag.NO);
        boolean addNameExist = baseExist && wareHouseName.isPresent();
        boolean modifyNameExist =
                Objects.nonNull(wareHouseModifyRequest.getWareId()) && wareHouseName.isPresent() && !wareHouseName.get().getWareId().equals(wareHouseModifyRequest.getWareId());

        if (addNameExist || modifyNameExist) {
            //名称重复
            throw new SbcRuntimeException(WareHouseErrorCode.WARE_NAME_EXIST,"名称重复");
        }
        KsBeanUtil.copyPropertiesThird(wareHouseModifyRequest, wareHouse);
        return wareHouse;
    }

    /**
     * 更新缓存
     * @param filed
     */
    private void saveAndFlushRedise(String filed){
        redisService.hdelete(WareHouseConstants.WARE_HOUSES,filed);
        redisService.hset(WareHouseConstants.WARE_HOUSES,
                WareHouseConstants.WARE_HOUSE_MAIN_FILED,
                JSON.toJSONString(this.list(WareHouseQueryRequest.builder().delFlag(DeleteFlag.NO).build())));
    }

    /**
     * 获取所有可用的分仓信息
     * @return
     */
    public List<WareHouseVO> queryWareHouses(List<Long> storeIdList, WareHouseType wareHouseType){
        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES,WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        if(StringUtils.isNotEmpty(wareHousesStr)){
            List<WareHouseVO> wareHouseVOS = JSON.parseArray(wareHousesStr, WareHouseVO.class);
            return wareHouseVOS.stream().filter(param -> storeIdList.contains(param.getStoreId())
                    &&param.getDelFlag().equals(DeleteFlag.NO)&&wareHouseType.equals(param.getWareHouseType())).collect(Collectors.toList());
        }
        List<WareHouse> list = this.list(WareHouseQueryRequest.builder()
                .delFlag(DeleteFlag.NO)
                .wareHouseType(wareHouseType)
                .storeIdList(storeIdList)
                .build());
        return list.stream().map(this::wrapperVo).collect(Collectors.toList());
    }

    /**
     * 获取所有可用的分仓信息
     * @return
     */
    public List<WareHouseVO> queryWareHousesByStoreid(Long storeId, WareHouseType wareHouseType){
        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES,WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        if(StringUtils.isNotEmpty(wareHousesStr)){
            List<WareHouseVO> wareHouseVOS = JSON.parseArray(wareHousesStr, WareHouseVO.class);
            return wareHouseVOS.stream().filter(param -> storeId.equals(param.getStoreId())
                    &&param.getDelFlag().equals(DeleteFlag.NO)&&wareHouseType.equals(param.getWareHouseType())).collect(Collectors.toList());
        }
        List<WareHouse> list = this.list(WareHouseQueryRequest.builder()
                .delFlag(DeleteFlag.NO)
                .wareHouseType(wareHouseType)
                .storeId(storeId)
                .build());
        return list.stream().map(this::wrapperVo).collect(Collectors.toList());
    }

    public WareHouseVO findBySelfErpIdAndDefaultFlagAndDelFlag(){
        WareHouse wareHouse = wareHouseRepository.findBySelfErpIdAndDefaultFlagAndDelFlag();
        WareHouseVO wareHouseVO = new WareHouseVO();
        BeanUtils.copyProperties(wareHouse,wareHouseVO);
        return wareHouseVO;
    }

    public WareHouseVO findBulkBySelfErpIdAndDefaultFlagAndDelFlag(){
        WareHouse wareHouse = wareHouseRepository.findBulkBySelfErpIdAndDefaultFlagAndDelFlag();
        WareHouseVO wareHouseVO = new WareHouseVO();
        BeanUtils.copyProperties(wareHouse,wareHouseVO);
        return wareHouseVO;
    }

}

