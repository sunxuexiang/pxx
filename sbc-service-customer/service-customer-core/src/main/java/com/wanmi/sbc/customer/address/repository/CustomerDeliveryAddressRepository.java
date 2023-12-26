package com.wanmi.sbc.customer.address.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.address.model.root.CustomerDeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 客户收货地址
 * Created by CHENLI on 2017/4/20.
 */
@Repository
@Transactional
public interface CustomerDeliveryAddressRepository extends JpaRepository<CustomerDeliveryAddress, String>,
        JpaSpecificationExecutor<CustomerDeliveryAddress> {

    /**
     * 修改为默认地址
     *
     * @param addressId
     * @param customerId
     */
    @Modifying
    @Query("update CustomerDeliveryAddress c set c.isDefaltAddress = 1 " +
            "where c.delFlag=0 and c.deliveryAddressId = :addressId and c.customerId = :customerId")
    void updateDefault(@Param("addressId") String addressId, @Param("customerId") String customerId);

    /**
     * 删除客户收货地址
     *
     * @param addressId
     */
    @Modifying
    @Query("update CustomerDeliveryAddress c set c.delFlag = 1 where c.deliveryAddressId = :addressId")
    void deleteAddress(@Param("addressId") String addressId);

    /**
     * 查询客户的收货地址
     *
     * @param customerId
     * @return
     */
    List<CustomerDeliveryAddress> findByCustomerIdAndDelFlagOrderByCreateTimeDesc(String customerId, DeleteFlag
            deleteFlag);

    /**
     * 查询该客户有多少条收货地址
     *
     * @param customerId
     * @return
     */
    @Query("select count(1) from CustomerDeliveryAddress c where c.delFlag=0 and c.customerId = :customerId")
    int countCustomerAddress(@Param("customerId") String customerId);

    /**
     * 查询该客户有多少条收货地址(所有的地址)
     *
     * @param customerId
     * @return
     */
    @Query("select count(1) from CustomerDeliveryAddress c where c.customerId = :customerId")
    int selectCountCustomerAddress(@Param("customerId") String customerId);


    /**
     * 查询客户有多少条收货地址(所有的地址)
     *
     * @return
     */
    @Query(value = "select * from customer_delivery_address c where c.del_flag = 0 and c.is_delivery != 1 and c.twon_id is null limit 10", nativeQuery = true)
    List<CustomerDeliveryAddress> selectCountCustomerAddress();

    /**
     * 修改为默认地址
     *
     * @param addressId
     * @param customerId
     */
    @Modifying
    @Query("update CustomerDeliveryAddress c set c.twonId = :twonId, c.twonName = :twonName , c.isDelivery = :isDelivery " +
            "where c.delFlag=0 and c.deliveryAddressId = :addressId and c.customerId = :customerId")
    int updateTwon(@Param("addressId") String addressId, @Param("customerId") String customerId, @Param("twonId") Long twonId, @Param("twonName") String twonName
    , @Param("isDelivery") Integer isDelivery);

    /**
     * 修改为默认地址
     *
     * @param addressId
     * @param customerId
     */
    @Modifying
    @Query("update CustomerDeliveryAddress c set c.isDelivery = :isDelivery " +
            "where c.delFlag=0 and c.deliveryAddressId = :addressId and c.customerId = :customerId")
    int updateTwonDelivery(@Param("addressId") String addressId, @Param("customerId") String customerId, @Param("isDelivery") Integer isDelivery);


    /**
     * 查询没有经纬度的收货地址
     * @param provinceId
     * @return
     */
    @Query(value = "SELECT * from customer_delivery_address  \n" +
            "WHERE n_lat is null  or n_lng is NULL  and province_id = ?1 LIMIT 300",nativeQuery = true)
    List<CustomerDeliveryAddress> getDataByProvinceIdAndJD(String provinceId);


    /**
     * 查询地址id和省市区街道详细地址
     * @param provinceId
     * @return
     */
    @Query(value = "SELECT\n" +
            "\t CONCAT(t2.`name`,t3.`name`,t4.`name`,t1.twon_name,t1.delivery_address) as detailed_address,\n" +
            "\t t1.delivery_address_id\n" +
            "FROM\n" +
            "\tcustomer_delivery_address t1\n" +
            " LEFT JOIN `sbc-setting`.region_copy t2 on t2.`code` = t1.province_id\n" +
            " LEFT JOIN `sbc-setting`.region_copy t3 on t3.`code` = t1.city_id\n" +
            " LEFT JOIN `sbc-setting`.region_copy t4 on t4.`code` = t1.area_id\n" +
            "WHERE\n" +
            "\tt1.n_lat IS NULL  \n" +
            "\tAND t1.province_id = ?1\n" +
            "\tAND t1.delivery_address is not null\n" +
            "\tAND t1.twon_name is not null\n" +
            "\tAND t1.del_flag = 0\n" +
            "\tLIMIT 300",nativeQuery = true)
    List<Object> getAdressDataByProvinceId(String provinceId);


    /**
     * 查询不符合条件的总数量
     * @param provinceId
     * @return
     */
    @Query(value = "SELECT count(*) as num from customer_delivery_address WHERE network_id is null and province_id = ?1",nativeQuery = true)
    Object getDataByProvinceIdNum(String provinceId);


    /**
     * 分页查询不符合条件的数据
     * @param provinceId
     * @param num
     * @param page
     * @return
     */
    @Query(value = "SELECT * from customer_delivery_address WHERE network_id is null and province_id = ?1 " +
            "and n_lat is not null  LIMIT ?2,?3",nativeQuery = true)
    List<CustomerDeliveryAddress> getDataByProvinceIdAndNetworkId(String provinceId,int num,int page);



    @Modifying
    @Transactional
    @Query(value = "update customer_delivery_address set n_lat = ?1, n_lng =?2 where delivery_address_id =?3",nativeQuery = true)
    int updateDeliveryJW(BigDecimal nLat,BigDecimal nLng,String deliveryAddressId);


    @Modifying
    @Transactional
    @Query(value = "update customer_delivery_address set network_id = null where network_id in ?1",nativeQuery = true)
    int updateNetworkId(List<Long> netWorkIds);

    @Modifying
    @Transactional
    @Query(value = "update customer_delivery_address set network_id = null where area_id = ?1",nativeQuery = true)
    int updateNetworkIdByArea(Long area);

    @Query(value = "SELECT * from customer_delivery_address WHERE area_id = ?1 and network_id is null and  n_lat is not null ",nativeQuery = true)
    List<CustomerDeliveryAddress> getDataByAreaId(Long areaId);
}
