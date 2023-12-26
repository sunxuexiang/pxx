package com.wanmi.sbc.goods.bidding.repository;

import com.wanmi.sbc.goods.bidding.model.root.Bidding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.wanmi.sbc.common.enums.DeleteFlag;
import java.util.Optional;
import java.util.List;

/**
 * <p>竞价配置DAO</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
@Repository
public interface BiddingRepository extends JpaRepository<Bidding, String>,
        JpaSpecificationExecutor<Bidding> {

    /**
     * 单个删除竞价配置
     * @author baijz
     */
    @Modifying
    @Query("update Bidding set delFlag = 1 where biddingId = ?1")
    void deleteById(String biddingId);

    /**
     * 批量删除竞价配置
     * @author baijz
     */
    @Modifying
    @Query("update Bidding set delFlag = 1 where biddingId in ?1")
    void deleteByIdList(List<String> biddingIdList);

    Optional<Bidding> findByBiddingIdAndDelFlag(String id, DeleteFlag delFlag);

}
