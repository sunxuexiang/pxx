package com.wanmi.sbc.goods.stockoutmanage.repository;

import com.wanmi.sbc.common.enums.ReplenishmentFlag;
import com.wanmi.sbc.goods.stockoutmanage.model.root.StockoutManage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.wanmi.sbc.common.enums.DeleteFlag;
import java.util.Optional;
import java.util.List;

/**
 * <p>缺货管理DAO</p>
 * @author tzx
 * @date 2020-05-27 11:37:26
 */
@Repository
public interface StockoutManageRepository extends JpaRepository<StockoutManage, String>,
        JpaSpecificationExecutor<StockoutManage> {

    /**
     * 单个删除缺货管理
     * @author tzx
     */
    @Modifying
    @Query("update StockoutManage set delFlag = 1 where stockoutId = ?1")
    void deleteById(String stockoutId);

    /**
     * 批量删除缺货管理
     * @author tzx
     */
    @Modifying
    @Query("update StockoutManage set delFlag = 1 where stockoutId in ?1")
    void deleteByIdList(List<String> stockoutIdList);

    Optional<StockoutManage> findByStockoutIdAndDelFlag(String id, DeleteFlag delFlag);


    StockoutManage findByGoodsInfoIdAndReplenishmentFlagAndWareIdAndDelFlag(String goodsInfoId, ReplenishmentFlag replenishmentFlag,Long wareId,DeleteFlag flag);

    @Query(value = "select * from stockout_manage t1 where t1.goods_info_id = ?1 and t1.replenishment_flag = 0 and t1.ware_id = ?2 and t1.del_flag = 0",nativeQuery = true)
    List<StockoutManage> findByGoodsInfoIdAndReplenishmentFlagAndWareIdAndDelFlagAndSoruce(String goodsInfoId,Long wareId);


    /**
     * 批量更新缺货标志位
     * @author tzx
     */
    @Modifying
    @Query("update StockoutManage set replenishmentFlag = 1 where stockoutId in ?1")
    void upddateFlagByIdList(List<String> stockoutIdList);
}
