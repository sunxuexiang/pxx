package com.wanmi.sbc.goods.brand.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品品牌数据源
 * Created by daiyitian on 2017/04/11.
 */
@Repository
public interface GoodsBrandRepository extends JpaRepository<GoodsBrand, Long>, JpaSpecificationExecutor<GoodsBrand>{

    List<GoodsBrand> findByBrandIdIn(List<Long> brandIds);

    List<GoodsBrand> findByBrandSeqNumAndDelFlag(Integer brandSeqNum, DeleteFlag delFlag);

    @Query("from GoodsBrand where brandSeqNum=?1 and brandId<>?2 and delFlag =?3")
    List<GoodsBrand> findByBrandSeqNum(Integer brandSeqNum, Long brandId, DeleteFlag delFlag);

    @Query("from GoodsBrand where  delFlag = 0")
    List<GoodsBrand> findByAllBrand();
    /**
     * 查询未删除的唯一品牌
     * @param brandName
     * @param flag
     * @return
     */
    GoodsBrand findByBrandNameAndDelFlag(String brandName, DeleteFlag flag);
}
