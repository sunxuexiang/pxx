package com.wanmi.sbc.customer.employee.repository;

import com.wanmi.sbc.customer.employee.model.root.EmployeeCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * @author lm
 * @date 2022/09/15 9:01
 */
@Repository
public interface EmployeeCopyRepository extends JpaRepository<EmployeeCopy, String>, JpaSpecificationExecutor<EmployeeCopy> {

    /**
     * 通过账号查询员工信息
     * @param account
     * @return
     */
    @Query("from EmployeeCopy e where e.delFlag = 0 and e.accountName = :account")
    EmployeeCopy findEmployeeCopyByAccountName(@Param("account") String account);

    /**
     * 解锁
     * @param employeeId
     * @return
     */
    @Modifying
    @Query("update EmployeeCopy e set e.loginErrorTime = 0,e.loginLockTime = null where e.employeeId = :employeeId")
    int unlockEmployeeCopy(@Param("employeeId") String employeeId);

    /**
     * 修改登录次数
     */
    @Modifying
    @Query("update EmployeeCopy e set e.loginErrorTime = IFNULL(e.loginErrorTime,0) + 1 where e.employeeId = :employeeId ")
    int updateLoginErrorCount(@Param("employeeId") String employeeId);


    /**
     * 修改锁时间
     * @param employeeId
     * @return
     */
    @Modifying
    @Query("update EmployeeCopy e set e.loginLockTime = ?2 where e.employeeId =?1")
    int updateLoginLockTime(String employeeId, LocalDateTime localDateTime);


    /**
     * 修改员工登录时间
     * @param employeeId
     * @param loginTime
     * @return rows
     */
    @Modifying
    @Query("update EmployeeCopy e set e.loginTime = ?2, e.loginErrorTime = 0, e.loginLockTime = null " +
            "where e.employeeId =?1")
    int updateLoginTime(String employeeId, LocalDateTime loginTime);

    /**
     * 根据电话号码查员工
     * @param phone
     * @return
     */
    @Query("from EmployeeCopy e where e.delFlag = 0 and e.employeeMobile = :phone")
    EmployeeCopy queryByPhone(@Param("phone") String phone);

    /**
     * 修改密码
     * @param employeeId
     * @param password
     * @return
     */
    @Modifying
    @Query("update EmployeeCopy e set e.accountPassword = :password where e.employeeId = :employeeId")
    int resetPassword(@Param("employeeId") String employeeId, @Param("password") String password);
}
