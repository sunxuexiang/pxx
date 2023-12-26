package com.wanmi.sbc.customer.parentcustomerrela.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaQueryRequest;
import com.wanmi.sbc.customer.parentcustomerrela.model.root.ParentCustomerRela;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.apache.commons.lang3.StringUtils;
import com.wanmi.sbc.common.util.XssUtils;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>子主账号关联关系动态查询条件构建器</p>
 * @author baijz
 * @date 2020-05-26 15:39:43
 */
public class ParentCustomerRelaWhereCriteriaBuilder {
    public static Specification<ParentCustomerRela> build(ParentCustomerRelaQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-父IdList
            if (CollectionUtils.isNotEmpty(queryRequest.getParentIdList())) {
                predicates.add(root.get("parentId").in(queryRequest.getParentIdList()));
            }

            // 父Id
            if (StringUtils.isNotEmpty(queryRequest.getParentId())) {
                predicates.add(cbuild.equal(root.get("parentId"), queryRequest.getParentId()));
            }

            // 模糊查询 - 会员Id
            if (StringUtils.isNotEmpty(queryRequest.getCustomerId())) {
                predicates.add(cbuild.like(root.get("customerId"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getCustomerId()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 批量查询-会员IdList
            if (CollectionUtils.isNotEmpty(queryRequest.getCustomerIdList())) {
                predicates.add(root.get("customerId").in(queryRequest.getCustomerIdList()));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
