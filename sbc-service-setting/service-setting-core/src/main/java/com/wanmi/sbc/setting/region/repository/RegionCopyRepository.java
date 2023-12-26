package com.wanmi.sbc.setting.region.repository;

import com.wanmi.sbc.setting.region.model.RegionCopy;
import com.wanmi.sbc.setting.region.model.RegionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 城市编号
 *
 * @author yitang
 * @version 1.0
 */
@Repository
public interface RegionCopyRepository extends JpaRepository<RegionCopy,Long> {

    /**
     * 根据地区code查询联动下的子地区
     * @param code 地区编码
     * @return
     */
    @Modifying
    @Query("from RegionCopy where parentCode=?1")
    List<RegionCopy> queryRegionByCode(Long code);

    @Query(value = "SELECT * from region_copy WHERE `code` = ?1",nativeQuery = true)
    List<RegionCopy> getRegionCopyByCode(Long code);

    /**
     * 根据城市编号查询城市名称
     * @param number
     * @return
     */
    @Query(value = "select * from region_copy r where r.code in (?1)",nativeQuery = true)
    List<RegionCopy> getRegionNumber(List<Long> number);

    @Query(value = "select * from region_copy r where r.level=?1",nativeQuery = true)
    List<RegionCopy> queryRegionByLevel(Integer level);

}
