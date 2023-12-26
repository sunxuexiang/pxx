package com.wanmi.sbc.account.wallet.service;

import com.wanmi.sbc.account.wallet.model.root.WalletRecord;
import com.wanmi.sbc.account.wallet.request.WalletRecordQueryRequest;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 动态构建钱包充值查询条件
 * @author: jiangxin
 * @create: 2021-11-03 9:34
 */
public class WalletRecordQueryBuilder {

    public static Specification<WalletRecord> build(WalletRecordQueryRequest request) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 客户帐号
            if (StringUtils.isNotEmpty(request.getCustomerAccount())) {
                predicates.add(cbuild.equal(root.get("customerAccount"), request.getCustomerAccount()));
            }

            // 交易流水号
            if (StringUtils.isNotEmpty(request.getRecordNo())) {
                predicates.add(cbuild.equal(root.get("recordNo"), request.getRecordNo()));
            }

            // 交易类型
            if (Objects.nonNull(request.getTradeType())) {
                predicates.add(cbuild.equal(root.get("tradeType"), request.getTradeType().toValue()));
            }
            //收支类型
            if (Objects.nonNull(request.getBudgetType())) {
                predicates.add(cbuild.equal(root.get("budgetType"), request.getBudgetType().toValue()));
            }
            //收支类型
            if (request.getBudgetTypeInt() != null) {
                predicates.add(cbuild.equal(root.get("budgetType"), request.getBudgetTypeInt()));
            }
            //关联单号
            if (StringUtils.isNotEmpty(request.getRelationOrderId())) {
                predicates.add(cbuild.equal(root.get("relationOrderId"), request.getRelationOrderId()));
            }
            //用户账户
            if (CollectionUtils.isNotEmpty(request.getCustomerNames())) {
                predicates.add(root.get("customerAccount").in(request.getCustomerNames()));
            }

            //时间根据月份
            if (Objects.nonNull(request.getStartTime()) && Objects.nonNull(request.getEndTime())) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("dealTime"), request.getEndTime()));// <=
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("dealTime"), request.getStartTime())); // >=
            }
            //备注(鲸贴明细模糊查询)
            if (StringUtils.isNotEmpty(request.getRemark())) {
                predicates.add(cbuild.like(root.get("remark"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(request.getRemark()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
