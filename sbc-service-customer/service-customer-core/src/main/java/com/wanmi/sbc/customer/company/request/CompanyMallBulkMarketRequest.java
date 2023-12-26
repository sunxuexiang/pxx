package com.wanmi.sbc.customer.company.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.company.model.root.CompanyMallBulkMarket;
import com.wanmi.sbc.customer.util.XssUtils;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * @program: sbc-backgroud
 * @description: aa
 * @author: gdq
 * @create: 2023-06-13 15:24
 **/
@Data
public class CompanyMallBulkMarketRequest extends BaseQueryRequest {

    /**
     * 市场IDS
     */
    private List<Long> marketIds;

    /**
     * 市场名称模糊查找
     */
    private String marketName;

    private Integer openStatus;

    private DeleteFlag deleteFlag;

    private String concatInfo;

    private Integer provinceId;

    public Specification<CompanyMallBulkMarket> getWhereCriteria() {
        return (root, query, build) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(marketName)) {
                predicates.add(build.like(root.get("marketName"), "%" + XssUtils.replaceLikeWildcard(marketName) + "%"));
            }
            if (Objects.nonNull(openStatus)) {
                predicates.add(build.equal(root.get("openStatus"), openStatus));
            }
            if (Objects.nonNull(deleteFlag)) {
                predicates.add(build.equal(root.get("delFlag"), deleteFlag.toValue()));
            }

            if (Objects.nonNull(provinceId)) {
                predicates.add(build.equal(root.get("provinceId"), provinceId));
            }

            if (CollectionUtils.isNotEmpty(marketIds)) {
                CriteriaBuilder.In in = build.in(root.get("marketId"));
                marketIds.forEach(id -> in.value(id));
                predicates.add(in);
            }
            if (StringUtils.isNotBlank(concatInfo)) {
                predicates.add(build.like(root.get("concatInfo"), "%" + XssUtils.replaceLikeWildcard(concatInfo.toLowerCase()) + "%"));
            }
            javax.persistence.criteria.Predicate[] p = predicates.toArray(new javax.persistence.criteria.Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : build.and(p);
        };
    }
}

