package com.wanmi.sbc.goods.goodswarestockdetail.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.goodswarestockdetail.model.root.GoodsWareStockDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * <p> 库存明细表DAO</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:24:37
 */
@Repository
public interface GoodsWareStockDetailRepository extends JpaRepository<GoodsWareStockDetail, Long>,
        JpaSpecificationExecutor<GoodsWareStockDetail> {

    /**
     * 单个删除 库存明细表
     * @author zhangwenchang
     */
    @Modifying
    @Query("update GoodsWareStockDetail set delFlag = 1 where id = ?1")
    void deleteById(Long id);

    /**
     * 批量删除 库存明细表
     * @author zhangwenchang
     */
    @Modifying
    @Query("update GoodsWareStockDetail set delFlag = 1 where id in ?1")
    void deleteByIdList(List<Long> idList);

    /**
     * 根据分仓库存表id批量删除 库存明细表
     * @author huapeiliang
     */
    @Modifying
    @Query("update GoodsWareStockDetail set delFlag = 1 where goodsWareStockId in ?1")
    void deleteByGoodsWareStockIdList(List<Long> goodsWareStockIds);

    Optional<GoodsWareStockDetail> findByIdAndDelFlag(Long id, DeleteFlag delFlag);

}
