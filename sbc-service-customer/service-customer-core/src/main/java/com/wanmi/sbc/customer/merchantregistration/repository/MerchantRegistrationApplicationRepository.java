package com.wanmi.sbc.customer.merchantregistration.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.merchantregistration.model.root.MerchantRegistrationApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 商家入驻申请数据源
 * @author hudong
 * @date 2023-06-17 11:24
 */
@Repository
public interface MerchantRegistrationApplicationRepository extends JpaRepository<MerchantRegistrationApplication, Long>, JpaSpecificationExecutor<MerchantRegistrationApplication> {

    /**
     * 根据id查询商家入驻申请信息
     *
     * @param applicationId
     * @param deleteFlag
     * @return
     */
    MerchantRegistrationApplication findByApplicationIdAndDelFlag(Long applicationId, DeleteFlag deleteFlag);

    /**
     * 根据商家名称查询申请记录
     *
     * @param merchantName
     * @param deleteFlag
     * @return
     */
    @Query("from MerchantRegistrationApplication m where m.merchantName = :merchantName and m.delFlag = :deleteFlag")
    MerchantRegistrationApplication findByMerchantNameAndDelFlag(@Param("merchantName") String merchantName, @Param("deleteFlag") DeleteFlag deleteFlag);

    /**
     * 根据商家联系方式查询申请记录
     *
     * @param merchantPhone
     * @return
     */
    @Query(value = "select * from merchant_registration_application t where t.merchant_phone = ?1 and t.del_flag = 0 limit 1",nativeQuery = true)
    MerchantRegistrationApplication findByMerchantPhoneAndDelFlag(String merchantPhone);


}
