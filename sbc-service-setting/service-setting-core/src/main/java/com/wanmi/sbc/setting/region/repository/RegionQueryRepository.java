package com.wanmi.sbc.setting.region.repository;

import com.wanmi.sbc.setting.region.model.RegionModel;
import org.springframework.data.jpa.repository.JpaRepository;
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
public interface RegionQueryRepository extends JpaRepository<RegionModel,Long> {

    /**
     * 根据城市编号查询城市名称
     * @param number
     * @return
     */
    @Query(value = "select * from region r where r.id in (?1)",nativeQuery = true)
    List<RegionModel> getRegionNumber(List<Long> number);
}
