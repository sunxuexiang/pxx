package com.wanmi.sbc.setting.villagesaddress.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.villagesaddress.model.root.VillagesAddressConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * @Description: 乡镇件地址配置dao
 * @Author: XinJiang
 * @Date: 2022/4/29 9:58
 */
@Repository
public interface VillagesAddressConfigRepository extends JpaRepository<VillagesAddressConfig,Long>, JpaSpecificationExecutor<VillagesAddressConfig> {

    /**
     * 通过地址id获取总数
     * @param provinceId
     * @param cityId
     * @param areaId
     * @param villageId
     * @return
     */
    Integer countByProvinceIdAndCityIdAndAreaIdAndVillageIdAndStoreId(Long provinceId, Long cityId, Long areaId, Long villageId, Long storeId);

    Integer countByProvinceIdAndCityIdAndAreaIdAndVillageIdAndStoreIdAndDelFlag(Long provinceId, Long cityId, Long areaId, Long villageId, Long storeId, DeleteFlag delFlag);

    @Query(value = "SELECT COUNT(1) FROM villages_address_config WHERE del_flag =0 AND village_id=?1 AND store_id=?2",nativeQuery = true)
    Integer getCountByVillageIdAndStoreId(Long villageId,Long storeId);

    @Query(value ="SELECT id,village_id,village_name FROM villages_address_config where del_flag =0 AND city_id in (:cityIdList)",nativeQuery = true)
    List<Object> findListVillageByCityList(@Param("cityIdList") List<Long> cityIdList);

    @Query(value ="SELECT id,village_id,village_name FROM villages_address_config where del_flag =0 AND village_id in (:villageIdList)",nativeQuery = true)
    List<Object> findListVillageByVillageIdList(@Param("villageIdList") List<Long> villageIdList);
}
