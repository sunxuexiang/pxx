package com.wanmi.sbc.customer.company.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.company.model.root.CompanyMallContractRelation;
import com.wanmi.sbc.customer.company.model.root.CompanyMallSupplierTab;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
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
public class CompanyMallContractRelationRequest extends BaseQueryRequest {

    private List<Long> ids;

    @ApiModelProperty(value = "签约类型")
    /**
    * @See
    */
    private Integer relationType;

    @ApiModelProperty(value = "公司Id")
    private Long companyInfoId;

    @ApiModelProperty(value = "公司Id")
    private List<Long> companyInfoIds;

    @ApiModelProperty(value = "店铺Id")
    private Long storeId;

    @ApiModelProperty(value = "店铺Ids")
    private List<Long> storeIds;

    /**
     * 删除标志
     */
    @ApiModelProperty(value = "删除标志")
    private DeleteFlag deleteFlag;

    @ApiModelProperty(value = "关联的值")
    private String relationValue;


    public Specification<CompanyMallContractRelation> getWhereCriteria() {
        return (root, query, build) -> {
            List<Predicate> predicates = new ArrayList<>();
            // in
            if (CollectionUtils.isNotEmpty(ids)) {
                CriteriaBuilder.In in = build.in(root.get("id"));
                ids.forEach(id -> in.value(id));
                predicates.add(in);
            }

            if (CollectionUtils.isNotEmpty(companyInfoIds)) {
                CriteriaBuilder.In in = build.in(root.get("companyInfoId"));
                companyInfoIds.forEach(id -> in.value(id));
                predicates.add(in);
            }

            if (CollectionUtils.isNotEmpty(storeIds)) {
                CriteriaBuilder.In in = build.in(root.get("storeId"));
                storeIds.forEach(id -> in.value(id));
                predicates.add(in);
            }
            if (Objects.nonNull(relationType)) {
                predicates.add(build.equal(root.get("relationType"), relationType));
            }
            if (Objects.nonNull(companyInfoId)) {
                predicates.add(build.equal(root.get("companyInfoId"), companyInfoId));
            }
            if (Objects.nonNull(storeId)) {
                predicates.add(build.equal(root.get("storeId"), storeId));
            }
            if (Objects.nonNull(relationValue)) {
                predicates.add(build.equal(root.get("relationValue"), relationValue));
            }
            predicates.add(build.equal(root.get("delFlag"), DeleteFlag.NO));
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : build.and(p);
        };


    }
}

