package com.wanmi.sbc.goods.goodslabelrela.repository;

import com.wanmi.sbc.goods.goodslabelrela.model.root.GoodsLabelRela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * <p>邀新统计DAO</p>
 * @author lvheng
 * @date 2021-04-23 14:20:19
 */
@Repository
public interface GoodsLabelRelaRepository extends JpaRepository<GoodsLabelRela, Long>,
        JpaSpecificationExecutor<GoodsLabelRela> {

    /**
     * 查询某个商品的全部标签
     * @param goodsId
     * @return
     */
    Optional<List<GoodsLabelRela>> findByGoodsId(String goodsId);


    /**
     * 删除某个商品的全部标签
     * @param goodsId
     */
    void deleteByGoodsId(String goodsId);

    /**
     * 根据LabelId查询数据
     * @param labelId
     * @return
     */
    Optional<List<GoodsLabelRela>> findByLabelId(Long labelId);

    /**
     * 根据GOodsIDs查询数据
     * @param goodsId
     * @return
     */
    Optional<List<GoodsLabelRela>> findByGoodsIdIn(List<String> goodsId);

    /**
     * 删除符合条件的数据
     * @param goodsId
     * @param labelId
     */
    @Modifying
    @Query("delete from GoodsLabelRela where labelId = ?2 and goodsId in ?1")
    Integer deleteByGoodsIdInAndLabelId(List<String> goodsId, Long labelId);
}
