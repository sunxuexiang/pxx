package com.wanmi.sbc.customer.workorder.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderQueryRequest;
import com.wanmi.sbc.customer.detail.model.root.CustomerDetail;
import com.wanmi.sbc.customer.model.root.Customer;
import com.wanmi.sbc.customer.workorder.model.root.WorkOrder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.apache.commons.lang3.StringUtils;
import com.wanmi.sbc.common.util.XssUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>工单动态查询条件构建器</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
public class WorkOrderWhereCriteriaBuilder {
    public static Specification<WorkOrder> build(WorkOrderQueryRequest queryRequest) {

        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //联查
            Join<WorkOrder, Customer> customerJoin = root.join("customer", JoinType.LEFT);
            // 批量查询-工单IdList
            if (CollectionUtils.isNotEmpty(queryRequest.getWorkOrderIdList())) {
                predicates.add(root.get("workOrderId").in(queryRequest.getWorkOrderIdList()));
            }

            // 工单Id
            if (StringUtils.isNotEmpty(queryRequest.getWorkOrderId())) {
                predicates.add(cbuild.equal(root.get("workOrderId"), queryRequest.getWorkOrderId()));
            }

            // 模糊查询 - 工单号
            if (StringUtils.isNotEmpty(queryRequest.getWorkOrderNo())) {
                predicates.add(cbuild.like(root.get("workOrderNo"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getWorkOrderNo()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 社会信用代码
            if (StringUtils.isNotEmpty(queryRequest.getSocialCreditCode())) {
                predicates.add(cbuild.like(root.get("socialCreditCode"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getSocialCreditCode()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 精确查询 - 已注册会员的Id
            if (StringUtils.isNotEmpty(queryRequest.getRegistedCustomerId())) {
                predicates.add(cbuild.equal(root.get("registedCustomerId"),queryRequest.getRegistedCustomerId()));
            }

            // 精确查询 - 将册会员的Id
            if (StringUtils.isNotEmpty(queryRequest.getApprovalCustomerId())) {
                predicates.add(cbuild.equal(root.get("approvalCustomerId"),queryRequest.getApprovalCustomerId()));
            }

            // 账号合并状态
            if (queryRequest.getAccountMergeStatus() != null) {
                predicates.add(cbuild.equal(root.get("accountMergeStatus"), queryRequest.getAccountMergeStatus()));
            }

            // 状态 0:待处理，1：已完成
            if (queryRequest.getStatus() != null) {
                predicates.add(cbuild.equal(root.get("status"), queryRequest.getStatus()));
            }

            // 大于或等于 搜索条件:创建时间开始
            if (queryRequest.getCreateTimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("createTime"),
                        queryRequest.getCreateTimeBegin()));
            }
            // 小于或等于 搜索条件:创建时间截止
            if (queryRequest.getCreateTimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("createTime"),
                        queryRequest.getCreateTimeEnd()));
            }

            // 大于或等于 搜索条件:修改时间开始
            if (queryRequest.getModifyTimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("modifyTime"),
                        queryRequest.getModifyTimeBegin()));
            }
            // 小于或等于 搜索条件:修改时间截止
            if (queryRequest.getModifyTimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("modifyTime"),
                        queryRequest.getModifyTimeEnd()));
            }

            // 大于或等于 搜索条件:删除时间开始
            if (queryRequest.getDeleteTiimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("deleteTiime"),
                        queryRequest.getDeleteTiimeBegin()));
            }
            // 小于或等于 搜索条件:删除时间截止
            if (queryRequest.getDeleteTiimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("deleteTiime"),
                        queryRequest.getDeleteTiimeEnd()));
            }

            // 删除标志位
            if (queryRequest.getDelFlag() != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }




            /**
             * 模糊查询客户名称
             */
            if (Objects.nonNull(queryRequest.getCustomerName()) && StringUtils.isNotEmpty(queryRequest.getCustomerName().trim())) {
                predicates.add(cbuild.like(customerJoin.get("customerDetail").get("customerName"),StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getCustomerName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            /**
             * 模糊查询客户联系方式
             */
            if (StringUtils.isNotBlank(queryRequest.getContactPhone())) {
                predicates.add(cbuild.like(customerJoin.get("customerDetail").get("contactPhone"),StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getContactPhone()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
