package com.wanmi.sbc.goods.goodslabel.repository;

import com.wanmi.sbc.goods.goodslabel.model.root.GoodsLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.wanmi.sbc.common.enums.DeleteFlag;
import java.util.Optional;
import java.util.List;

/**
 * <p>导航配置DAO</p>
 * @author lvheng
 * @date 2021-04-19 11:09:28
 */
@Repository
public interface GoodsLabelRepository extends JpaRepository<GoodsLabel, Long>,
        JpaSpecificationExecutor<GoodsLabel> {

    /**
     * 单个删除导航配置
     * @author lvheng
     */
    @Modifying
    @Query("update GoodsLabel set delFlag = 1 where id = ?1")
    void deleteById(Long id);

    /**
     * 批量删除导航配置
     * @author lvheng
     */
    @Modifying
    @Query("update GoodsLabel set delFlag = 1 where id in ?1")
    void deleteByIdList(List<Long> idList);

    Optional<GoodsLabel> findByIdAndDelFlag(Long id, DeleteFlag delFlag);


    Integer countByDelFlag(DeleteFlag deleteFlag);

    @Query("select max(gl.sort) from GoodsLabel gl where gl.delFlag = 0")
    Optional<Integer> findMaxSort();


    /**
     * 查询前20条未删除的数据,防止之后业务变动
     * @return
     */
    @Query(value = "select * from goods_label where del_flag = 0 limit 20",nativeQuery = true)
    List<GoodsLabel> findTopByDelFlag();

    Integer countByNameAndDelFlag(String labelName,DeleteFlag deleteFlag);

}
