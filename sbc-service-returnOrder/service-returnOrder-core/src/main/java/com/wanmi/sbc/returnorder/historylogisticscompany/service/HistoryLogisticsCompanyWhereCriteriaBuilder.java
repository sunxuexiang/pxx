package com.wanmi.sbc.returnorder.historylogisticscompany.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.returnorder.api.request.historylogisticscompany.HistoryLogisticsCompanyQueryRequest;
import com.wanmi.sbc.returnorder.historylogisticscompany.model.root.HistoryLogisticsCompany;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>物流公司历史记录动态查询条件构建器</p>
 * @author fcq
 * @date 2020-11-09 17:32:23
 */
public class HistoryLogisticsCompanyWhereCriteriaBuilder {
    public static Specification<HistoryLogisticsCompany> build(HistoryLogisticsCompanyQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-主键List
            if (CollectionUtils.isNotEmpty(queryRequest.getIdList())) {
                predicates.add(root.get("id").in(queryRequest.getIdList()));
            }

            // 主键
            if (StringUtils.isNotEmpty(queryRequest.getId())) {
                predicates.add(cbuild.equal(root.get("id"), queryRequest.getId()));
            }

            // 查询 - 会员id
            if (StringUtils.isNotEmpty(queryRequest.getCustomerId())) {
                predicates.add(cbuild.equal(root.get("customerId"), queryRequest.getCustomerId()));
            }

            // 模糊查询 - 订单id
            if (StringUtils.isNotEmpty(queryRequest.getOrderId())) {
                predicates.add(cbuild.like(root.get("orderId"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getOrderId()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 物流公司名称
            if (StringUtils.isNotEmpty(queryRequest.getLogisticsName())) {
                predicates.add(cbuild.like(root.get("logisticsName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getLogisticsName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 物流公司电话
            if (StringUtils.isNotEmpty(queryRequest.getLogisticsPhone())) {
                predicates.add(cbuild.like(root.get("logisticsPhone"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getLogisticsPhone()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 收货站点
            if (StringUtils.isNotEmpty(queryRequest.getReceivingSite())) {
                predicates.add(cbuild.like(root.get("receivingSite"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getReceivingSite()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 删除标志
            if (queryRequest.getDelFlag() != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }
            //自建物流
            if (queryRequest.getSelfFlag() != null) {
                predicates.add(cbuild.equal(root.get("selfFlag"), queryRequest.getSelfFlag()));
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

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
