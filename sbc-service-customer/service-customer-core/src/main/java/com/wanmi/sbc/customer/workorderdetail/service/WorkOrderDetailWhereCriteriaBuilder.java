package com.wanmi.sbc.customer.workorderdetail.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.customer.api.request.workorderdetail.WorkOrderDetailQueryRequest;
import com.wanmi.sbc.customer.workorderdetail.model.root.WorkOrderDetail;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.apache.commons.lang3.StringUtils;
import com.wanmi.sbc.common.util.XssUtils;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>工单明细动态查询条件构建器</p>
 * @author baijz
 * @date 2020-05-17 16:03:58
 */
public class WorkOrderDetailWhereCriteriaBuilder {
    public static Specification<WorkOrderDetail> build(WorkOrderDetailQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-工单处理明细IdList
            if (CollectionUtils.isNotEmpty(queryRequest.getWorkOrderDelIdList())) {
                predicates.add(root.get("workOrderDelId").in(queryRequest.getWorkOrderDelIdList()));
            }

            // 工单处理明细Id
            if (StringUtils.isNotEmpty(queryRequest.getWorkOrderDelId())) {
                predicates.add(cbuild.equal(root.get("workOrderDelId"), queryRequest.getWorkOrderDelId()));
            }

            // 大于或等于 搜索条件:处理时间开始
            if (queryRequest.getDealTimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("dealTime"),
                        queryRequest.getDealTimeBegin()));
            }
            // 小于或等于 搜索条件:处理时间截止
            if (queryRequest.getDealTimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("dealTime"),
                        queryRequest.getDealTimeEnd()));
            }

            // 处理状态
            if (queryRequest.getStatus() != null) {
                predicates.add(cbuild.equal(root.get("status"), queryRequest.getStatus()));
            }

            // 模糊查询 - 处理建议
            if (StringUtils.isNotEmpty(queryRequest.getSuggestion())) {
                predicates.add(cbuild.like(root.get("suggestion"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getSuggestion()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 工单Id
            if (StringUtils.isNotEmpty(queryRequest.getWorkOrderId())) {
                predicates.add(cbuild.like(root.get("workOrderId"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getWorkOrderId()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
