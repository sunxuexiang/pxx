package com.wanmi.sbc.goods.goodsattribute.repository;

import com.wanmi.sbc.goods.devanninggoodsinfo.model.root.DevanningGoodsInfo;
import com.wanmi.sbc.goods.goodsattribute.root.GoodsAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsAttributeReponsitory extends JpaRepository<GoodsAttribute, Long>, JpaSpecificationExecutor<GoodsAttribute> {
    @Query("update GoodsAttribute p set p.delFlag = 1 where p.attributeId = :attributeId")
    @Modifying
    int modifyDelFlagById(String attributeId);



    @Query(value = "select * from goods_attribute WHERE attribute_id = ?1",nativeQuery = true)
    GoodsAttribute getByAttributeId(String attributeId);
}
