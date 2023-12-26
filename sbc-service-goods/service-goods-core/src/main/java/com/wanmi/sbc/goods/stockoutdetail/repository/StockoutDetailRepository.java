package com.wanmi.sbc.goods.stockoutdetail.repository;

import com.wanmi.sbc.goods.stockoutdetail.model.root.StockoutDetail;
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
 * @date 2020-05-27 11:37:12
 */
@Repository
public interface StockoutDetailRepository extends JpaRepository<StockoutDetail, String>,
        JpaSpecificationExecutor<StockoutDetail> {

    /**
     * 单个删除缺货管理
     * @author tzx
     */
    @Modifying
    @Query("update StockoutDetail set delFlag = 1 where stockoutDetailId = ?1")
    void deleteById(String stockoutDetailId);

    /**
     * 批量删除缺货管理
     * @author tzx
     */
    @Modifying
    @Query("update StockoutDetail set delFlag = 1 where stockoutDetailId in ?1")
    void deleteByIdList(List<String> stockoutDetailIdList);

    Optional<StockoutDetail> findByStockoutDetailIdAndDelFlag(String id, DeleteFlag delFlag);

    /**
     * 批量删除缺货管理
     * @author tzx
     */
    @Modifying
    @Query("update StockoutDetail set delFlag = 1 where stockoutId in ?1")
    void deleteByStockoutIdIs(List<String> stockouIdList);

}
