package com.wanmi.sbc.customer.company.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.company.model.root.CompanyMallReturnGoodsAddress;
import com.wanmi.sbc.customer.company.model.root.CompanyMallSupplierTab;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.checkerframework.checker.fenum.qual.AwtFlowLayout;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @program: sbc-backgroud
 * @description: aa
 * @author: gdq
 * @create: 2023-06-13 15:24
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMallReturnGoodsAddressRequest extends BaseQueryRequest {

    /**
     * 推荐Id
     */
    private List<Long> Ids;

    private Long companyInfoId;

    private Long storeId;

    private DeleteFlag deleteFlag;

    public Specification<CompanyMallReturnGoodsAddress> getWhereCriteria() {
        return (root, query, build) -> {
            List<Predicate> predicates = new ArrayList<>();
            // in
            if (CollectionUtils.isNotEmpty(Ids)) {
                CriteriaBuilder.In in = build.in(root.get("id"));
                Ids.forEach(id -> {
                    in.value(id);
                });
                predicates.add(in);
            }
            if (Objects.nonNull(deleteFlag)) {
                predicates.add(build.equal(root.get("delFlag"), deleteFlag));
            }
            if (Objects.nonNull(companyInfoId)) {
                predicates.add(build.equal(root.get("companyInfoId"), companyInfoId));
            }
            if (Objects.nonNull(storeId)) {
                predicates.add(build.equal(root.get("storeId"), storeId));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : build.and(p);
        };
    }
}

