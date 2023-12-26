package com.wanmi.sbc.setting.imonlineserviceitem.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceModifyRequest;
import com.wanmi.sbc.setting.imonlineserviceitem.root.ImOnlineServiceItem;
import com.wanmi.sbc.setting.onlineserviceitem.model.root.OnlineServiceItem;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * <p>ImOnlineServiceRepositoryDao</p>
 * @author SGY
 * @date 2023-06-05 16:10:28
 */
@Repository
public interface ImOnlineServiceItemRepository extends JpaRepository<ImOnlineServiceItem, Integer>,
        JpaSpecificationExecutor<OnlineServiceItem> {

    /**
     * 通过店铺id appKey 查询在线客服座席
     * @param imServiceItemId
     * @param deleteFlag
     * @return findByAgeOrderByLastnameDesc
     */
    List<ImOnlineServiceItem> findByImOnlineServiceIdAndDelFlagOrderByCreateTimeDesc(Integer imOnlineServiceId, DeleteFlag deleteFlag);

    /**
     * 批量删除在线客服下面的座席
     * @param imServiceItemId
     */
    void deleteByImOnlineServiceId(Integer imOnlineServiceId);

    /**
     * 查询Im号是否重复
     * @param serverAccount
     * @return
     */
    @Query("from ImOnlineServiceItem l where l.delFlag = 0 and l.customerServiceAccount in  ?1 ")
    List<ImOnlineServiceItem> checkDuplicateAccount(List<String> serverAccount);
    /**
     * 查询Im信息
     * @param customerServiceAccount
     * @return
     */
    @Query("from ImOnlineServiceItem l where l.delFlag = 0 and l.customerServiceAccount =  ?1  and l.companyInfoId =  -1 ")
    List<ImOnlineServiceItem> findPlatformImList( String customerServiceAccount, String companyInfoId);

    List<ImOnlineServiceItem> findByCustomerServiceAccount(String customerServiceAccount);

    List<ImOnlineServiceItem> findByCompanyInfoIdAndDelFlag(Long companyInfo, DeleteFlag delFlag);

    List<ImOnlineServiceItem> findByImOnlineServiceIdAndDelFlagAndServiceStatusOrderByCreateTimeDesc(Integer onlineServiceId, DeleteFlag deleteFlag, int i);

    @Query(value = "select DISTINCT customer_service_account from im_online_service_item where phone_no = ?1 and customer_service_account is not null", nativeQuery = true)
    List<String> findCustomerServiceAccountByMobile(String mobileNumber);

    List<ImOnlineServiceItem> findByCompanyInfoId(Long companyInfoId);

    @Query(value = "select distinct(store_id) from im_online_service_item where store_id is NOT null and store_id != -1", nativeQuery = true)
    List<Long> findHaveCustomerServiceStoreIds();

    List<ImOnlineServiceItem> findByCustomerServiceAccountAndCompanyInfoId(String customerServiceAccount, Long companyInfoId);

    List<ImOnlineServiceItem> findByCompanyInfoIdAndPhoneNo(Long companyInfoId, String phoneNo);
}
