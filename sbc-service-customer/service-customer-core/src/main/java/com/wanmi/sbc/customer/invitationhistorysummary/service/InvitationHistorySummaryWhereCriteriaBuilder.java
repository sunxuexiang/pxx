package com.wanmi.sbc.customer.invitationhistorysummary.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryQueryRequest;
import com.wanmi.sbc.customer.invitationhistorysummary.model.root.InvitationHistorySummary;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.apache.commons.lang3.StringUtils;
import com.wanmi.sbc.common.util.XssUtils;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>邀新历史汇总计表动态查询条件构建器</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
public class InvitationHistorySummaryWhereCriteriaBuilder {
    public static Specification<InvitationHistorySummary> build(InvitationHistorySummaryQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-业务员IDList
            if (CollectionUtils.isNotEmpty(queryRequest.getEmployeeIdList())) {
                predicates.add(root.get("employeeId").in(queryRequest.getEmployeeIdList()));
            }

            // 业务员ID
            if (StringUtils.isNotEmpty(queryRequest.getEmployeeId())) {
                predicates.add(cbuild.equal(root.get("employeeId"), queryRequest.getEmployeeId()));
            }

            // 总邀新数
            if (queryRequest.getTotalCount() != null) {
                predicates.add(cbuild.equal(root.get("totalCount"), queryRequest.getTotalCount()));
            }

            // 总订单金额
            if (queryRequest.getTotalTradePrice() != null) {
                predicates.add(cbuild.equal(root.get("totalTradePrice"), queryRequest.getTotalTradePrice()));
            }

            // 总商品数
            if (queryRequest.getTotalGoodsCount() != null) {
                predicates.add(cbuild.equal(root.get("totalGoodsCount"), queryRequest.getTotalGoodsCount()));
            }

            // 总订单数
            if (queryRequest.getTradeTotal() != null) {
                predicates.add(cbuild.equal(root.get("tradeTotal"), queryRequest.getTradeTotal()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
