package com.wanmi.sbc.goods.freight.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.freight.model.root.FreightTemplateGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sunkun on 2018/5/2.
 */
@Repository
public interface FreightTemplateGoodsRepository extends JpaRepository<FreightTemplateGoods, Long> {

    @Query(value = "from FreightTemplateGoods a where a.deliverWay =?2 AND a.storeId = ?1 and a.defaultFlag = 1 and a.delFlag = 0 order by a.freightTempId asc")
    List<FreightTemplateGoods> queryByDefault(Long storeId,DeliverWay deliverWay);

    /**
     * 获取店铺下所有单品运费模板
     *
     * @param storeId
     * @param delFlag
     * @return
     */
    @Query("from FreightTemplateGoods f where f.deliverWay =:deliverWay AND f.storeId = :storeId and f.delFlag = :delFlag order by f.defaultFlag desc,f.createTime desc")
    List<FreightTemplateGoods> queryAll(@Param("storeId") Long storeId, @Param("delFlag") DeleteFlag delFlag,@Param("deliverWay") DeliverWay deliverWay);

    /**
     * 获取单品运费模板
     *
     * @param freightTempId
     * @return
     */
    @Query("from FreightTemplateGoods f where f.freightTempId = :freightTempId and f.delFlag = 0")
    FreightTemplateGoods queryById(@Param("freightTempId") Long freightTempId);

    /**
     * 查询店铺下未删除的单品运费模板总数
     *
     * @param storeId
     * @param delFlag
     * @return
     */
    //int countByStoreIdAndDelFlag(Long storeId, DeleteFlag delFlag);

    int countByStoreIdAndDelFlagAndDeliverWay(Long storeId, DeleteFlag delFlag, DeliverWay deliverWay);

    /**
     * 根据单品名称查询
     *
     * @param storeId
     * @param freightTempName
     * @return
     */
    @Query("from FreightTemplateGoods f where f.deliverWay =:deliverWay and f.storeId = :storeId and f.freightTempName = :freightTempName and f.delFlag = 0")
    FreightTemplateGoods queryByFreighttemplateName(@Param("storeId") Long storeId, @Param("freightTempName") String freightTempName,@Param("deliverWay") DeliverWay deliverWay);

    @Query("from FreightTemplateGoods f where f.freightTempId in :freightTempIds")
    List<FreightTemplateGoods> queryByFreightTempIds(@Param("freightTempIds") List<Long> freightTempIds);

    /**
     * 获取平台端配送到家运费模板
     * @return
     */
    @Query("from FreightTemplateGoods f where f.deliverWay =7 AND f.storeId = -1 and f.delFlag = 0 order by f.defaultFlag desc,f.createTime desc")
    List<FreightTemplateGoods> queryPlatformDeliveryToStoreTempList();

    /**
     * 获取平台端默认的配送到家运费模板
     * @return
     */
    @Query("from FreightTemplateGoods f where f.deliverWay =7 AND f.storeId = -1 and f.delFlag = 0 and f.defaultFlag=1 order by f.createTime desc")
    List<FreightTemplateGoods> queryDefaultDeliveryToStoreTempList();

    @Modifying
    @Query(value = "update freight_template_goods set default_flag=?2 where freight_temp_id=?1",nativeQuery = true)
    void updateTemplateDefaultFlag(Long freightTempId,Integer defaultFlag);

    @Modifying
    @Query(value = "update freight_template_goods set default_flag=?2 where store_id=?1 and deliver_way=?3",nativeQuery = true)
    void updateTemplateDefaultFlagByStoreId(Long storeId,Integer defaultFlag,Integer deliverWay);
}
