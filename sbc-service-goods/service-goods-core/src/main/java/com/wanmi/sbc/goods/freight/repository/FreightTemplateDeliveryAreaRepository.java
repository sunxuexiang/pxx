package com.wanmi.sbc.goods.freight.repository;

import com.wanmi.sbc.goods.freight.model.root.FreightTemplateDeliveryArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>配送到家范围DAO</p>
 *
 * @author zhaowei
 * @date 2021-03-25 16:57:57
 */
@Repository
public interface FreightTemplateDeliveryAreaRepository extends JpaRepository<FreightTemplateDeliveryArea, Long>,
        JpaSpecificationExecutor<FreightTemplateDeliveryArea> {

    /**
     * 单个删除配送到家范围
     *
     * @author zhaowei
     */
    @Modifying
    @Query("update FreightTemplateDeliveryArea set delFlag = 1 where id = ?1")
    void deleteById(Long id);

    /**
     * 批量删除配送到家范围
     *
     * @author zhaowei
     */
    @Modifying
    @Query("update FreightTemplateDeliveryArea set delFlag = 1 where id in ?1")
    int deleteByIdList(List<Long> idList);

    /**
     * 根据配送类型获取配置商家编号
     *
     * @author zhaowei
     */
    @Modifying
    @Query(value = "select store_id from freight_template_delivery_area where destination_type =?1 and open_flag =1",nativeQuery = true)
    List<Long> getUseDeliveryToStoreSupplierList(Integer destinationType);

    @Modifying
    @Query(value = "update freight_template_delivery_area set del_flag = 1 where del_flag =0 and store_id in ?1 and ware_id = ?2 and destination_type =?3",nativeQuery = true)
    void updateDeliveryAreaByStore(List<Long> storeList,Long wareId,Integer destinationType);

    @Modifying
    @Query(value = "update freight_template_delivery_area set open_flag = ?1 where store_id = ?2 and destination_type=?3",nativeQuery = true)
    void updateOpenFlag(Integer openFlag,Long storeId,Integer destinationType);

    @Query(value = "select * from freight_template_delivery_area where store_id = ?1 and destination_type=?2",nativeQuery = true)
    List<FreightTemplateDeliveryArea> findByStoreIdAndDestinationType(Long storeId,Integer destinationType);

    @Query(value = "select store_id from freight_template_delivery_area where store_id!=-1 and destination_type =?1 and company_info_id=-1",nativeQuery = true)
    List<Long> getBossDeliveryCfg(Integer destinationType);

}
