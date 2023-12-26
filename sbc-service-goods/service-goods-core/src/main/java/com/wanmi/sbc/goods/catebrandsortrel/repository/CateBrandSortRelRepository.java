package com.wanmi.sbc.goods.catebrandsortrel.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.catebrandsortrel.model.root.CateBrandSortRel;
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
 * @date 2021-04-08 11:24:32
 */
@Repository
public interface CateBrandSortRelRepository extends JpaRepository<CateBrandSortRel, Long>,
        JpaSpecificationExecutor<CateBrandSortRel> {

    /**
     * 根据cateId删除数据
     * @param cateId
     */
    @Modifying
    @Query("update CateBrandSortRel set delFlag = 1 where cateId = ?1")
    void deleteByCateId(Long cateId);

    /**
     * 批量删除类目品牌排序表
     * @author lvheng
     */
    @Modifying
    @Query("update CateBrandSortRel set delFlag = 1 where cateId in ?1")
    void deleteByIdList(List<Long> cateIdList);

    @Query("FROM CateBrandSortRel cbs WHERE cbs.brandId =?2 and cbs.cateId = ?1 and cbs.delFlag = ?3")
    Optional<CateBrandSortRel> findById(Long cateId,Long brandId,DeleteFlag deleteFlag);


    Integer countByCateIdAndSerialNoAndDelFlag(Long cateId, Integer serialNo,DeleteFlag deleteFlag);


}
