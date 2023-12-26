package com.wanmi.sbc.goods.biddinggoods.repository;

import com.wanmi.sbc.goods.biddinggoods.model.root.BiddingGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.wanmi.sbc.common.enums.DeleteFlag;
import java.util.Optional;
import java.util.List;

/**
 * <p>竞价商品DAO</p>
 * @author baijz
 * @date 2020-08-05 16:34:44
 */
@Repository
public interface BiddingGoodsRepository extends JpaRepository<BiddingGoods, String>,
        JpaSpecificationExecutor<BiddingGoods> {

    /**
     * 单个删除竞价商品
     * @author baijz
     */
    @Modifying
    @Query("update BiddingGoods set delFlag = 1 where biddingGoodsId = ?1")
    void deleteById(String biddingGoodsId);

    /**
     * 批量删除竞价商品
     * @author baijz
     */
    @Modifying
    @Query("update BiddingGoods set delFlag = 1 where biddingGoodsId in ?1")
    void deleteByIdList(List<String> biddingGoodsIdList);

    Optional<BiddingGoods> findByBiddingGoodsIdAndDelFlag(String id, DeleteFlag delFlag);

}
