package com.wanmi.sbc.customer.company.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.company.model.root.CompanyMallSupplierRecommend;
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
public class CompanyMallSupplierRecommendRequest extends BaseQueryRequest {

    @ApiModelProperty(value = "公司名称")
    private String companyInfoName;

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 数据是否用
     */
    @ApiModelProperty(value = "删除标识")
    private DeleteFlag deleteFlag;

    @ApiModelProperty(value = "公司IDs")
    private List<Long> companyInfoIds;

    private List<Long> ids;

    @ApiModelProperty(value = "公司IDs")
    private List<Long> storeIds;

    // 1:设置过的，0：未设置，-1 全部
    private Integer assignSort;


    public Specification<CompanyMallSupplierRecommend> getWhereCriteria() {
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
            // like
            if (StringUtils.isNotBlank(companyInfoName)) {
                predicates.add(build.like(root.get("companyInfoName"), "%" + XssUtils.replaceLikeWildcard(companyInfoName) + "%"));
            }
            // e
            if (Objects.nonNull(deleteFlag)) {
                predicates.add(build.equal(root.get("delFlag"), deleteFlag));
            }
            if (Objects.nonNull(id)) {
                predicates.add(build.equal(root.get("id"), id));
            }
            if (Objects.nonNull(assignSort)) {
                if (assignSort == 1){
                    predicates.add(build.greaterThan(root.get("assignSort"), 0));
                }else if (assignSort == 0){
                    predicates.add(build.isNull(root.get("assignSort")));
                }
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : build.and(p);
        };
    }
}

