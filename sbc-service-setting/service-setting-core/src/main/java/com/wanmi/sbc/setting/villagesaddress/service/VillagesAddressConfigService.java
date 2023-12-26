package com.wanmi.sbc.setting.villagesaddress.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.setting.api.request.villagesaddress.VillagesAddressConfigQueryRequest;
import com.wanmi.sbc.setting.villagesaddress.model.root.VillagesAddressConfig;
import com.wanmi.sbc.setting.villagesaddress.repository.VillagesAddressConfigRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description: 乡镇件地址配置service
 * @author: XinJiang
 * @time: 2022/4/29 9:59
 */
@Service
public class VillagesAddressConfigService {

    @Autowired
    private VillagesAddressConfigRepository villagesAddressConfigRepository;

    /**
     * 批量保存乡镇件地址配置信息
     * @param villagesAddressConfigList
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchAdd(List<VillagesAddressConfig> villagesAddressConfigList) {
        villagesAddressConfigRepository.saveAll(villagesAddressConfigList);
    }

    /**
     * 通过id删除地址配置信息
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    public void delByIds (List<Long> ids) {
        ids.forEach(id ->  villagesAddressConfigRepository.deleteById(id));
    }

    /**
     * 分页获取配置信息
     * @param request
     * @return
     */
    public Page<VillagesAddressConfig> page(VillagesAddressConfigQueryRequest request) {
        request.setDelFlag(DeleteFlag.NO);
        return villagesAddressConfigRepository.findAll(VillagesAddressConfigWhereCriteriaBuilder.build(request), request.getPageRequest());
    }

    /**
     * 获取所有乡镇件地址配置信息
     * @return
     */
    public List<VillagesAddressConfig> findAll(VillagesAddressConfigQueryRequest request) {
        request.setDelFlag(DeleteFlag.NO);
        if (Objects.nonNull(request.getSort())) {
            return villagesAddressConfigRepository.findAll(VillagesAddressConfigWhereCriteriaBuilder.build(request),request.getSort());
        } else {
            return villagesAddressConfigRepository.findAll(VillagesAddressConfigWhereCriteriaBuilder.build(request));
        }
    }

    /**
     * 通过省、市、区、街道id获取配置信息
     * @param provinceId
     * @param cityId
     * @param areaId
     * @param villageId
     */
    public Integer getCountByAllId(Long provinceId,Long cityId,Long areaId,Long villageId,Long storeId) {
        return villagesAddressConfigRepository.countByProvinceIdAndCityIdAndAreaIdAndVillageIdAndStoreIdAndDelFlag(provinceId, cityId, areaId, villageId,storeId,DeleteFlag.NO);
    }

    public Integer getCountByVillageIdAndStoreId(Long villageId,Long storeId) {
        return villagesAddressConfigRepository.getCountByVillageIdAndStoreId(villageId,storeId);
    }

    public List<VillagesAddressConfig> findListVillageByCityList(VillagesAddressConfigQueryRequest request) {
        List<Object> objectList = villagesAddressConfigRepository.findListVillageByCityList(request.getCityIdList());
        return wrapFromOjbToVillages(objectList);
    }

    public List<VillagesAddressConfig> findListVillageByVillageIdList(VillagesAddressConfigQueryRequest request) {
        List<Object> objectList = villagesAddressConfigRepository.findListVillageByVillageIdList(request.getVillagesIds());
        return wrapFromOjbToVillages(objectList);
    }

    private static List<VillagesAddressConfig> wrapFromOjbToVillages(List<Object> objectList) {
        List<VillagesAddressConfig> villagesAddressConfigs = new ArrayList<>(objectList.size());
        if(CollectionUtils.isNotEmpty(objectList)){
            objectList.forEach(item->{
                Object[] results = StringUtil.cast(item, Object[].class);
                VillagesAddressConfig villagesAddressConfig =new VillagesAddressConfig();
                villagesAddressConfig.setVillageId(Long.parseLong(results[1].toString()));
                villagesAddressConfig.setVillageName(Objects.toString(results[2]));
                villagesAddressConfigs.add(villagesAddressConfig);
            });
        }
        return villagesAddressConfigs;
    }
}
