package com.wanmi.sbc.pay.service;

import com.wanmi.sbc.pay.api.request.CcbStatementDetPageRequest;
import com.wanmi.sbc.pay.model.root.CcbStatementDet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 建行对账单分账明细动态查询条件构建器
 *
 * @author hudong
 * @date 2023-09-19 16:12:49
 */
public class CcbStatementDetWhereCriteriaBuilder {

    public static Specification<CcbStatementDet> build(CcbStatementDetPageRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //  支付流水号
            if (StringUtils.isNotEmpty(queryRequest.getPyOrdrNo())) {
                predicates.add(cbuild.equal(root.get("pyOrdrNo"), queryRequest.getPyOrdrNo()));
            }
            //  主订单编号
            if (StringUtils.isNotEmpty(queryRequest.getMainOrdrNo())) {
                predicates.add(cbuild.equal(root.get("mainOrdrNo"), queryRequest.getMainOrdrNo()));
            }
            // 分账账户
            if (StringUtils.isNotEmpty(queryRequest.getRcvprtMktMrchId())) {
                predicates.add(cbuild.equal(root.get("rcvprtMktMrchId"), queryRequest.getRcvprtMktMrchId()));
            }
            // 分账日期
            if (Objects.nonNull(queryRequest.getClrgDt())) {
                predicates.add(cbuild.equal(root.get("clrgDt"), queryRequest.getClrgDt()));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

}
