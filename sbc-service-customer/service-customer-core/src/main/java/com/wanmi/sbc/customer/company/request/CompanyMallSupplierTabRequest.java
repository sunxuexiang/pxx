package com.wanmi.sbc.customer.company.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.company.model.root.CompanyMallBulkMarket;
import com.wanmi.sbc.customer.company.model.root.CompanyMallSupplierTab;
import com.wanmi.sbc.customer.util.XssUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
@ApiModel
public class CompanyMallSupplierTabRequest extends BaseQueryRequest {

    @ApiModelProperty(value = "商家商城分类")
    private List<Long> Ids;

    @ApiModelProperty(value = "商家商城Id")
    private Long id;

    @ApiModelProperty(value = "商家商城分类")
    private String name;

    /**
     * 删除标志
     */
    @ApiModelProperty(value = "删除标志")
    private DeleteFlag deleteFlag;

    private Integer openStatus;


    public Specification<CompanyMallSupplierTab> getWhereCriteria() {
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
            if (StringUtils.isNotBlank(name)) {
                predicates.add(build.like(root.get("tabName"), "%" + XssUtils.replaceLikeWildcard(name) + "%"));
            }
            if (Objects.nonNull(deleteFlag)) {
                predicates.add(build.equal(root.get("delFlag"), deleteFlag));
            }
            if (Objects.nonNull(id)) {
                predicates.add(build.equal(root.get("id"), id));
            }

            if (Objects.nonNull(openStatus)) {
                predicates.add(build.equal(root.get("openStatus"), openStatus));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : build.and(p);
        };
    }
}

