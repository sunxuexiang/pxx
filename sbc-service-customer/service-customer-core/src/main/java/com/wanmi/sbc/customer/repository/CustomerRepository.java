package com.wanmi.sbc.customer.repository;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.enums.CustomerTag;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.model.root.Customer;
import com.wanmi.sbc.customer.model.root.CustomerBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 会员数据层
 * Created by CHENLI on 2017/4/18.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String>, JpaSpecificationExecutor<Customer> {

    /**
     * 根据会员ID查询会员信息
     *
     * @param customerId
     * @return
     */
    Customer findByCustomerIdAndDelFlag(String customerId, DeleteFlag deleteFlag);

    /**
     * 批量查询会员信息
     *
     * @param idList
     * @return
     */
    List<Customer> findByCustomerIdIn(Collection<String> idList);


    /**
     * 批量查询会员信息(未删除)
     *
     * @param idList
     * @return
     */
    List<Customer> findByCustomerIdInAndDelFlag(Collection<String> idList,DeleteFlag deleteFlag);

    /**
     * 检验账户是否存在
     *
     * @param customerAccount
     * @param deleteFlag
     * @return
     */
    Customer findByCustomerAccountAndDelFlag(String customerAccount, DeleteFlag deleteFlag);

    /**
     * 查询成长值大于0的客户
     *
     * @return
     */
    @Query("from Customer c where c.growthValue > 0")
    List<Customer> findHasGrowthValueCustomer();

    /**
     * 更新主账号信息
     *
     * @param customerId
     * @param customerId
     * @return
     */
    @Modifying
    @Query("update Customer c set c.pointsAvailable =c.pointsAvailable + :pointsAvailable where c.delFlag = 0 and c.customerId = " +
            ":customerId")
    int updateParentMergeAccount(@Param("customerId") String customerId,@Param("pointsAvailable")  Long pointsAvailable);

    @Modifying
    @Query("update Customer c set c.pointsAvailable =0 ,c.socialCreditCode = :socialCreditCode,c.enterpriseStatusXyy =2," +
            "c.parentCustomerId = :parentCustomerId, c.customerErpId = :customerErpId,c.customerRegisterType = :customerRegisterType" +
            ",c.businessLicenseUrl =:businessLicenseUrl where c.delFlag = 0 and c.customerId = :customerId")
    int updateChildAccount(@Param("customerId") String customerId,@Param("socialCreditCode") String socialCreditCode,
                           @Param("parentCustomerId") String parentCustomerId, @Param("customerErpId") String customerErpId);
    /**
     * 审核客户状态
     *
     * @param checkState
     * @param customerId
     * @return
     */
    @Modifying
    @Query("update Customer c set c.checkState = :checkState,c.checkTime = :checkTime where c.delFlag = 0 and c.customerId = " +
            ":customerId")
    int checkCustomerState(@Param("checkState") CheckState checkState, @Param("customerId") String customerId, @Param(
            "checkTime") LocalDateTime checkTime);

    /**
     * 审核企业会员
     *
     * @param enterpriseCheckState
     * @param customerId
     * @return
     */
    @Modifying
    @Query("update Customer c set c.enterpriseCheckState = :enterpriseCheckState, c.checkState.checkState = :checkState, " +
            "c.enterpriseCheckReason = :enterpriseCheckReason, " +
            "c.checkTime = :checkTime where c.delFlag = 0 and c.customerId = :customerId")
    int checkEnterpriseCustomer(@Param("enterpriseCheckState") EnterpriseCheckState enterpriseCheckState,
                                @Param("checkState") CheckState checkState,
                                @Param("enterpriseCheckReason") String enterpriseCheckReason,
                                @Param("customerId") String customerId,
                                @Param("checkTime") LocalDateTime checkTime);


    /**
     * 审核企业会员
     *
     * @param enterpriseStatusXyy
     * @param customerId
     * @return
     */
    @Modifying
    @Query("update Customer c set c.enterpriseCheckState = :enterpriseStatusXyy, " +
            "c.enterpriseCheckReason = :enterpriseCheckReason, " +
            "c.checkTime = :checkTime where c.delFlag = 0 and c.customerId = :customerId")
    int verifyEnterpriseCustomer(@Param("enterpriseStatusXyy") EnterpriseCheckState enterpriseStatusXyy,
                                @Param("enterpriseCheckReason") String enterpriseCheckReason,
                                @Param("customerId") String customerId,
                                @Param("checkTime") LocalDateTime checkTime);


    /**
     * 批量删除会员
     *
     * @param customerIds
     * @return
     */
    @Modifying
    @Query("update Customer c set c.delFlag = 1 where c.delFlag = 0 and c.customerId in :customerIds")
    int deleteByCustomerId(@Param("customerIds") List<String> customerIds);

    /**
     * 删除会员等级时，把该等级下的所有会员转到默认等级下
     *
     * @param defaultLevelId
     * @param customerLevelId
     * @return
     */
    @Modifying
    @Query("update Customer c set c.customerLevelId = :defaultLevelId where c.delFlag = 0 and c.customerLevelId = " +
            ":customerLevelId")
    int updateCustomerLevel(@Param("defaultLevelId") Long defaultLevelId, @Param("customerLevelId") Long
            customerLevelId);

    /**
     * 解锁
     *
     * @param customerId
     * @return
     */
    @Modifying
    @Query("update Customer e set e.loginLockTime = null, e.loginErrorCount = 0 where e.customerId = :customerId")
    int unlockCustomer(@Param("customerId") String customerId);

    /**
     * 修改登录次数
     *
     * @param customerId
     */
    @Modifying
    @Query("update Customer e set e.loginErrorCount = IFNULL(e.loginErrorCount,0) + 1 where e.customerId = :customerId")
    int updateloginErrorCount(@Param("customerId") String customerId);

    /**
     * 修改锁时间
     *
     * @param customerId customerId
     * @return
     */
    @Modifying
    @Query("update Customer e set e.loginLockTime = ?2 where e.customerId =?1")
    int updateLoginLockTime(String customerId, LocalDateTime localDateTime);

    /**
     * 修改客户登录时间
     *
     * @param customerId customerId
     * @param loginTime  loginTime
     * @param loginIp    loginIp
     * @return rows
     */
    @Modifying
    @Query("update Customer e set e.loginTime = ?2, e.loginErrorCount = 0, e.loginLockTime = null, e.loginIp = ?3 " +
            "where e.customerId =?1")
    int updateLoginTime(String customerId, LocalDateTime loginTime, String loginIp);


    /**
     * 修改绑定手机号
     *
     * @param customerId
     */
    @Modifying
    @Query("update Customer e set e.customerAccount = :customerAccount where e.delFlag = 0 and e.customerId = " +
            ":customerId")
    int updateCustomerAccount(@Param("customerId") String customerId, @Param("customerAccount") String customerAccount);

    /**
     * 扣除会员积分
     *
     * @param customerId
     * @param points
     * @return
     */
    @Modifying
    @Query("update Customer c set c.pointsAvailable = c.pointsAvailable - :points, c.pointsUsed = c.pointsUsed + " +
            ":points where c.customerId = :customerId and c.pointsAvailable >= :points")
    int updateCustomerPoints(@Param("customerId") String customerId, @Param("points") Long points);

    /**
     * 修改积分
     *
     * @param customerId
     * @param pointsAvailable
     * @return
     */
    @Modifying
    @Query("update Customer c set c.pointsAvailable = :pointsAvailable " +
            " where c.customerId = :customerId and c.pointsAvailable >= 0")
    int updateCustomerNumPoint(@Param("customerId") String customerId, @Param("pointsAvailable") Long pointsAvailable);

    /**
     * 修改会有的业务员
     *
     * @param employeeIdPre employeeIdPre
     * @param employeeId    employeeId
     * @return rows
     */
    @Modifying
    @Query("update CustomerDetail c set c.employeeId = :employeeId where c.delFlag = 0 and c.employeeId = " +
            ":employeeIdPre")
    int updateCustomerByEmployeeId(@Param("employeeIdPre") String employeeIdPre, @Param("employeeId") String
            employeeId);

    /**
     * 查询成长值达到x的会员id列表
     *
     * @param growthValue 成长值
     * @return
     */
    @Query("select c.customerId from Customer c  where c.growthValue >= :growthValue and c.delFlag = 0")
    List<String> findByGrowthValue(@Param("growthValue") Long growthValue);

    @Query(value = "select new com.wanmi.sbc.customer.model.root.CustomerBase(c.customerId,c.customerAccount) FROM " +
            "Customer c " +
            " where c.customerId = :customerId and c.delFlag = :delFlag")
    CustomerBase getBaseCustomerByCustomerId(@Param("customerId") String customerId, @Param("delFlag") DeleteFlag
            delFlag);

    /**
     * 根据会员ID查询会员等级ID
     *
     * @param customerIds
     * @return
     */
    @Query("select new com.wanmi.sbc.customer.model.root.CustomerBase(c.customerId,c.customerLevelId) FROM Customer c" +
            " where  c.customerId in :customerIds")
    List<CustomerBase> findCustomerLevelIdByCustomerIds(@Param("customerIds") List<String> customerIds);

    /**
     * 根据会员ID查询会员等级ID
     *
     * @param customerIds
     * @return
     */
    @Query("select new com.wanmi.sbc.customer.model.root.CustomerBase(c.customerId,c.customerAccount)  FROM Customer " +
            "c where  c.customerId in :customerIds")
    List<CustomerBase> getBaseCustomerByCustomerIds(@Param("customerIds") List<String> customerIds);

    @Query("select c.customerId from Customer c  where c.delFlag = 0 and c.checkState = 1 ")
    List<String> findCustomerIdByPageable(Pageable pageable);

    @Query("select c.customerId from Customer c  where c.delFlag = 0 and c.checkState = 1 and (c.parentCustomerId is null  or  c.parentCustomerId='') ")
    List<String> findCustomerIdNoParentByPageable(Pageable pageable);

    @Query("select c.customerId from Customer c  where c.delFlag = 0 and c.checkState = 1 and c.customerLevelId in " +
            ":customerLevelIds")
    List<String> findCustomerIdByCustomerLevelIds(@Param("customerLevelIds") List<Long> customerLevelIds, Pageable
            pageable);

    @Query("select c.customerId from Customer c  where c.delFlag = 0 and c.checkState = 1 and c.customerLevelId in " +
            ":customerLevelIds and (c.parentCustomerId is null  or  c.parentCustomerId='') ")
    List<String> findCustomerIdByCustomerLevelIdsParent(@Param("customerLevelIds") List<Long> customerLevelIds, Pageable
            pageable);

    /**
     * 编辑会员标签
     *
     * @param customerId
     * @param customerTag
     * @return
     */
    @Modifying
    @Query("update Customer c set c.customerTag = :customerTag where c.customerId = :customerId ")
    int modifyCustomerTag(@Param("customerId") String customerId, @Param("customerTag") CustomerTag customerTag);

    /**
     * 编辑大客户标识
     * @param customerId
     * @param vipFlag
     * @return
     */
    @Modifying
    @Query("update Customer c set c.vipFlag = :vipFlag where c.customerId = :customerId")
    int modifyVipFlag(@Param("customerId") String customerId, @Param("vipFlag")DefaultFlag vipFlag);

    /**
     * 根据parentId查询子账户
     * @param parentCustomerId
     * @return
     */
    List<Customer> findAllByParentCustomerIdAndDelFlag(String parentCustomerId, DeleteFlag deleteFlag);


    /**
     * 查询第一个注册的社会信用代码
     * @param SocialCreditCode
     * @return
     */
    List<Customer> findAllBySocialCreditCodeAndDelFlag(String SocialCreditCode ,DeleteFlag deleteFlag);

    /**
     * 查询第一个注册的社会信用代码
     * @param customerAccount
     * @return
     */
    @Modifying
    @Query(value = "update Customer c set c.erpAsyncFlag = 1 where c.customerAccount = ?1 and c.delFlag = 0")
    int updateCustomerAsyncFlag(String customerAccount);

    /**
     * 更新用户来源渠道
     * @param channel
     * @return
     */
    @Modifying
    @Query(value = "update Customer c set c.channel = ?2 where c.customerId = ?1")
    int modifyCustomerChannel(String customerId,String channel);


    /**
     * 根据手机号查询会员信息
     * @param customerAccountList
     * @return
     */
    @Query("select new com.wanmi.sbc.customer.model.root.CustomerBase(c.customerId,c.customerAccount)  FROM Customer " +
            "c where  c.customerAccount in :customerAccountList and c.delFlag = 0")
    List<CustomerBase> findAllByCustomerAccountInAndDelFlag(@Param("customerAccountList") List<String> customerAccountList);

    /**
     * 需要比较出新用户
     * 查询所有用户的ID
     */

    @Query("select c.customerId from Customer c")
    List<String> getAllCustomerId();



    /**
     *
     */
    @Query(value = "select invitee_account,count(1) from customer where del_flag=0 and" +
            " invitee_account is not null and invitee_account != '' AND if(?1 =''||?1 is null,1=1,invitee_account LIKE %?1%)  group by invitee_account ",nativeQuery = true)
    Page<Object> queryGroupByInviteeAccount(String account, Pageable pageable);


    /**
     * 修改原有的白鲸管家
     *
     * @param employeeIdPre employeeIdPre
     * @param employeeId    employeeId
     * @return rows
     */
    @Modifying
    @Query("update CustomerDetail c set c.managerId = :employeeId where c.delFlag = 0 and c.managerId = " +
            ":employeeIdPre")
    int updateCustomerManagerByEmployeeId(@Param("employeeIdPre") String employeeIdPre, @Param("employeeId") String
            employeeId);

    /**
     * 根据会员等级ID查询会员ID
     * @param levelIds
     * @return
     */
    @Query("select c.customerId from Customer c  where c.delFlag = 0 and c.checkState = 1 and c.customerLevelId in :levelIds")
    List<String> getCustomerIdsByLevelIds(@Param("levelIds") List<Long> levelIds);
}
