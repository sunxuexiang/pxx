package com.wanmi.sbc.goods.warehouse.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouseDetail;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouseStock;
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
public interface WareHouseStockRepository extends JpaRepository<WareHouseStock, Integer>,
        JpaSpecificationExecutor<WareHouseStock> {


    @Query(value="select gsr.* from goods_stock_rel gsr where gsr.ware_house_id = ?1 and gsr.sku_id = ?2 order by gsr.sort_stock_name" , nativeQuery = true)
    List<WareHouseStock> findByWareHouseIdAndSkuId(String wareHouseId, String skuId);
}
