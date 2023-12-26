package com.wanmi.sbc.pay.repository;

import com.wanmi.sbc.pay.model.root.CcbRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/14 10:53
 */
@Repository
public interface CcbRuleRepository extends JpaRepository<CcbRule, Long>, JpaSpecificationExecutor<CcbRule> {
    CcbRule findByClrgRuleId(String clrgRuleId);

}