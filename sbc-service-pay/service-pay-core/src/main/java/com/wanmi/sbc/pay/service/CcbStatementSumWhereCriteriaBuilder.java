package com.wanmi.sbc.pay.service;

import com.wanmi.sbc.pay.api.request.CcbStatementSumPageRequest;
import com.wanmi.sbc.pay.model.root.CcbStatementSum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 建行对账单汇总动态查询条件构建器
 *
 * @author hudong
 * @date 2023-09-11 16:12:49
 */
public class CcbStatementSumWhereCriteriaBuilder {

    public static Specification<CcbStatementSum> build(CcbStatementSumPageRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();

            //  主订单编号
            if (StringUtils.isNotEmpty(queryRequest.getClrgTxnsrlno())) {
                predicates.add(cbuild.equal(root.get("clrgTxnsrlno"), queryRequest.getClrgTxnsrlno()));
            }
            //  市场商户编号
            if (StringUtils.isNotEmpty(queryRequest.getMktMrchId())) {
                predicates.add(cbuild.equal(root.get("mktMrchId"), queryRequest.getMktMrchId()));
            }
            //  市场商户名称
            if (StringUtils.isNotEmpty(queryRequest.getMktMrchNm())) {
                predicates.add(cbuild.equal(root.get("mktMrchNm"), queryRequest.getMktMrchNm()));
            }
            // 交易完成时间
            if (StringUtils.isNotEmpty(queryRequest.getClrgStcd())) {
                predicates.add(cbuild.equal(root.get("clrgStcd"), queryRequest.getClrgStcd()));
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
