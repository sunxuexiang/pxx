package com.wanmi.sbc.goods.warehouse.repository;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * <p>仓库表DAO</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@Repository
public interface WareHouseRepository extends JpaRepository<WareHouse, Long>,
        JpaSpecificationExecutor<WareHouse> {

    /**
     * 单个删除仓库表
     * @author zhangwenchang
     */
    @Modifying
    @Query("update WareHouse set delFlag = 1 where wareId = ?1")
    void deleteById(Long wareId);

    /**
     * 批量删除仓库表 —— 这里是物理删除
     * @author zhangwenchang
     */
    @Modifying
    @Query("delete from WareHouse where wareId in ?1")
    void deleteByIdList(List<Long> wareIdList);

    Optional<WareHouse> findByWareIdAndStoreIdAndDelFlag(Long id, Long storeId, DeleteFlag delFlag);

    /**
     * 去除默认仓
     * @author huapeiliang
     */
    @Modifying
    @Query("update WareHouse set defaultFlag = 0 where defaultFlag = 1 and storeId = ?1")
    void cancelDefaultFlagByStoreId(Long storeId);

    List<WareHouse> findAllByStoreIdAndDelFlagAndWareHouseType(Long storeId, DeleteFlag deleteFlag, WareHouseType wareHouseType);

    Optional<WareHouse> findFirstByStoreIdAndWareCodeAndDelFlag(Long storeId, String wareCode, DeleteFlag deleteFlag);

    Optional<WareHouse> findFirstByStoreIdAndWareNameAndDelFlag(Long storeId, String wareName, DeleteFlag deleteFlag);

    @Query(value = "SELECT\n" +
            "\twh.*\n" +
            "FROM\n" +
            "\tware_house wh\n" +
            "\tLEFT JOIN ware_house_city whc ON whc.ware_id = wh.ware_id \n" +
            "WHERE\n" +
            "\t wh.store_id = ?1 \n" +
            "\tAND whc.province_id = ?2 \n" +
            "\tAND whc.city_id = ?3 ",nativeQuery = true)
    WareHouse queryWareHouseByStoreIdAndProvinceIdAndCityId(Long storeId, Long provinceId, Long cityId);

    @Query(value = "SELECT * from ware_house WHERE default_flag =2" ,nativeQuery = true)
    List<WareHouse> queryWareHouseByAndDfAndDefaultFlag(int defaultFlag);

    WareHouse findByStoreIdAndDefaultFlag(Long storeId, DefaultFlag defaultFlag);

    List<WareHouse> findByWareIdIn(List<Long> wareIds);

    List<WareHouse> findByStoreIdIn(List<Long> storeIds);

    //获取零售默认仓库
    @Query("from WareHouse e where e.delFlag = 0 and e.defaultFlag = 0 and e.selfErpId = '001' ")
    WareHouse findBySelfErpIdAndDefaultFlagAndDelFlag();


    //获取零售默认仓库
    @Query("from WareHouse e where e.delFlag = 0 and e.defaultFlag = 2 and e.selfErpId = '001' ")
    WareHouse findBulkBySelfErpIdAndDefaultFlagAndDelFlag();

}
