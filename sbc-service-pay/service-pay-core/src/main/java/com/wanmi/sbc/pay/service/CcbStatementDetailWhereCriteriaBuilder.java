package com.wanmi.sbc.pay.service;

import com.wanmi.sbc.pay.api.request.CcbStatementDetailPageRequest;
import com.wanmi.sbc.pay.model.root.CcbStatementDetail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 建行对账单明细动态查询条件构建器
 *
 * @author hudong
 * @date 2023-09-11 16:12:49
 */
public class CcbStatementDetailWhereCriteriaBuilder {

    public static Specification<CcbStatementDetail> build(CcbStatementDetailPageRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //  支付流水号
            if (StringUtils.isNotEmpty(queryRequest.getOrdrNo())) {
                predicates.add(cbuild.equal(root.get("ordrNo"), queryRequest.getOrdrNo()));
            }
            //  主订单编号
            if (StringUtils.isNotEmpty(queryRequest.getMainOrdrNo())) {
                predicates.add(cbuild.equal(root.get("mainOrdrNo"), queryRequest.getMainOrdrNo()));
            }

            // 交易完成时间
            if (StringUtils.isNotEmpty(queryRequest.getTxnDt())) {
                predicates.add(cbuild.equal(root.get("txnDt"), queryRequest.getTxnDt()));
            }


            // 大于或等于 搜索条件:创建时间开始
//            if (queryRequest.getCreateTimeBegin() != null) {
//                predicates.add(cbuild.greaterThanOrEqualTo(root.get("createTime"),
//                        queryRequest.getCreateTimeBegin()));
//            }
            // 小于或等于 搜索条件:创建时间截止
//            if (queryRequest.getCreateTimeEnd() != null) {
//                predicates.add(cbuild.lessThanOrEqualTo(root.get("createTime"),
//                        queryRequest.getCreateTimeEnd()));
//            }
            //模糊查询
            //            if (StringUtils.isNotEmpty(queryRequest.getMainOrdrNo())) {
//                predicates.add(cbuild.like(root.get("mainOrdrNo"), StringUtil.SQL_LIKE_CHAR
//                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getMainOrdrNo()))
//                        .concat(StringUtil.SQL_LIKE_CHAR)));
//            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

}
