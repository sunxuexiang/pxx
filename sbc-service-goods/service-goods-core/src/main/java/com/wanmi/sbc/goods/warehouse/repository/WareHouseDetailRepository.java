package com.wanmi.sbc.goods.warehouse.repository;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouseDetail;
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
public interface WareHouseDetailRepository extends JpaRepository<WareHouseDetail, Integer>,
        JpaSpecificationExecutor<WareHouseDetail> {

    /**
     * 单个删除仓库表
     * @author zhangwenchang
     */
    @Modifying
    @Query("update WareHouseDetail set delFlag = 1 where id = ?1")
    void deleteById(Long id);

    Optional<WareHouseDetail> findByWareIdAndDelFlag(Long wareId, DeleteFlag delFlag);
}
