package com.wanmi.sbc.setting.systemcancellationpolicy.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.systemcancellationpolicy.model.root.SystemCancellationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>隐私政策DAO</p>
 * @author yangzhen
 * @date 2020-09-23 14:52:35
 */
@Repository
public interface SystemCancellationPolicyRepository extends JpaRepository<SystemCancellationPolicy, String>,
        JpaSpecificationExecutor<SystemCancellationPolicy> {

    /**
     * 单个删除隐私政策
     * @author yangzhen
     */
    @Modifying
    @Query("update SystemCancellationPolicy set delFlag = 1 where cancellationPolicyId = ?1")
    void deleteById(String privacyPolicyId);

    /**
     * 批量删除隐私政策
     * @author yangzhen
     */
    @Modifying
    @Query("update SystemCancellationPolicy set delFlag = 1 where cancellationPolicyId in ?1")
    int deleteByIdList(List<String> privacyPolicyIdList);



    /**
     * 查询隐私政策
     *
     * @param deleteFlag
     * @return
     */
    List<SystemCancellationPolicy> findByDelFlag(DeleteFlag deleteFlag);

}
