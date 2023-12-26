package com.wanmi.sbc.customer.address.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.address.model.root.CustomerDeliveryAddress;
import com.wanmi.sbc.customer.address.model.root.CustomerDeliveryAddressExce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 客户收货地址
 * Created by CHENLI on 2017/4/20.
 */
@Repository
@Transactional
public interface CustomerDeliveryAddressExceRepository extends JpaRepository<CustomerDeliveryAddressExce, String>,
        JpaSpecificationExecutor<CustomerDeliveryAddressExce> {

    /**
     * 修改为默认地址
     *
     * @param addressId
     * @param customerId
     */
    @Modifying
    @Query("update CustomerDeliveryAddress c set c.delFlag = :delFlag " +
            "where c.deliveryAddressId = :addressId and c.customerId = :customerId")
    void updateDefault(@Param("addressId") String addressId, @Param("customerId") String customerId,@Param("delFlag") String delFlag);



    /**
     * 查询该客户有多少条收货地址
     *
     * @param customerId
     * @return
     */
    @Query("from CustomerDeliveryAddress c where  c.deliveryAddressId = :addressId and c.customerId = :customerId")
    CustomerDeliveryAddressExce countCustomerAddress(@Param("addressId") String addressId, @Param("customerId") String customerId);

    @Modifying
    @Query(value = "delete from customer_delivery_address_exce where delivery_address_id = ?1",nativeQuery = true)
    void delete( String addressId);
}
