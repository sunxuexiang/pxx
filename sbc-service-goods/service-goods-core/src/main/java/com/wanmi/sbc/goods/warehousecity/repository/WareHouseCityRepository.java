package com.wanmi.sbc.goods.warehousecity.repository;

import com.wanmi.sbc.goods.warehousecity.model.root.WareHouseCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p> 仓库地区表DAO</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:28:33
 */
@Repository
public interface WareHouseCityRepository extends JpaRepository<WareHouseCity, Long>,
        JpaSpecificationExecutor<WareHouseCity> {

    /**
     * 批量删除店铺资源资源分类表
     * @author lq
     */
    @Modifying
    @Query("update WareHouseCity set delFlag = 1 where id in ?1")
    int deleteByIdList(List<Long> idList);

    @Modifying
    @Query("delete WareHouseCity where wareId = ?1")
    int deleteByWareHouseId(Long id);
}
