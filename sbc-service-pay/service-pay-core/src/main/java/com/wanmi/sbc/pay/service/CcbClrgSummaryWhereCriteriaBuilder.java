package com.wanmi.sbc.pay.service;

import com.wanmi.sbc.pay.api.request.CcbClrgSummaryPageRequest;
import com.wanmi.sbc.pay.model.root.CcbClrgSummary;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 建行对账单分账汇总动态查询条件构建器
 *
 * @author hudong
 * @date 2023-09-23 16:12:49
 */
public class CcbClrgSummaryWhereCriteriaBuilder {

    public static Specification<CcbClrgSummary> build(CcbClrgSummaryPageRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();

            //  分账账户
            if (StringUtils.isNotEmpty(queryRequest.getMktMrchId())) {
                predicates.add(cbuild.equal(root.get("mktMrchId"), queryRequest.getMktMrchId()));
            }

            // 大于或等于 搜索条件:创建时间开始
            if (queryRequest.getClrgDtBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("clrgDt"),
                        queryRequest.getClrgDtBegin()));
            }
            // 小于或等于 搜索条件:创建时间截止
            if (queryRequest.getClrgDtEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("clrgDt"),
                        queryRequest.getClrgDtEnd()));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

}
