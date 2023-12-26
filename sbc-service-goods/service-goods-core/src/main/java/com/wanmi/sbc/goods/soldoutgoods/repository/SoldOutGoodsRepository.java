package com.wanmi.sbc.goods.soldoutgoods.repository;

import com.wanmi.sbc.goods.soldoutgoods.model.root.SoldOutGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * <p>类目品牌排序表DAO</p>
 * @author lvheng
 * @date 2021-04-10 15:09:50
 */
@Repository
public interface SoldOutGoodsRepository extends JpaRepository<SoldOutGoods, String>,
        JpaSpecificationExecutor<SoldOutGoods> {

    @Modifying
    @Query(value = "delete from sold_out_goods where goods_id in ?1 ",nativeQuery = true)
    void deleteByGoodsIds(List<String> ids);
}
