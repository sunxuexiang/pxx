package com.wanmi.sbc.customer.merchantregistration.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.customer.merchantregistration.model.root.MerchantRegistrationApplication;
import com.wanmi.sbc.customer.util.XssUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 商家入驻申请请求类
 * @author hudong
 * @date 2023-06-17 11:54
 */
@Data
public class MerchantRegistrationRequest extends BaseQueryRequest {

    /**
     * 申请id列表
     */
    private List<Long> applicationIds;

    /**
     * 模糊商家名称
     */
    private String merchantName;

    /**
     * 精确公司名称
     */
    private String companyName;

    /**
     * '商家联系方式'
     */
    private String merchantPhone;

    /**
     * 对接人
     */
    private String contactPerson;



    /**
     * 商家删除状态
     */
    private DeleteFlag deleteFlag;

    /**
     * 是否处理 0:否, 1:是
     */
    private Integer handleFlag;

    /**
     * 搜索条件:申请时间开始
     */
    @ApiModelProperty(value = "搜索条件:申请时间开始")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTimeBegin;
    /**
     * 搜索条件:申请时间截止
     */
    @ApiModelProperty(value = "搜索条件:申请时间截止")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTimeEnd;



    /**
     * 封装公共条件
     *
     * @return
     */
    public Specification<MerchantRegistrationApplication> getWhereCriteria() {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询idList
            if (CollectionUtils.isNotEmpty(applicationIds)) {
                CriteriaBuilder.In in = cbuild.in(root.get("applicationId"));
                applicationIds.forEach(id -> {
                    in.value(id);
                });
                predicates.add(in);
            }

//            if (CollectionUtils.isNotEmpty(applicationIds)) {
//                predicates.add(root.get("applicationId").in(applicationIds));
//            }

            if (StringUtils.isNotBlank(merchantName)) {
                predicates.add(cbuild.like(root.get("merchantName"), "%" + XssUtils.replaceLikeWildcard(merchantName)
                        + "%"));
            }
            if (StringUtils.isNotBlank(companyName)) {
                predicates.add(cbuild.equal(root.get("companyName"), XssUtils.replaceLikeWildcard(companyName)));
            }

            if (StringUtils.isNotBlank(merchantPhone)) {
                predicates.add(cbuild.equal(root.get("merchantPhone"), XssUtils.replaceLikeWildcard(merchantPhone)));
            }

            if (StringUtils.isNotBlank(contactPerson)) {
                predicates.add(cbuild.equal(root.get("contactPerson"), XssUtils.replaceLikeWildcard(contactPerson)));
            }

            if (Objects.nonNull(deleteFlag)) {
                predicates.add(cbuild.equal(root.get("delFlag"), deleteFlag.toValue()));
            }

            if (Objects.nonNull(handleFlag)) {
                predicates.add(cbuild.equal(root.get("handleFlag"), handleFlag));
            }

            // 大于或等于 搜索条件:申请时间开始
            if (createTimeBegin != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("createTime"),
                        createTimeBegin));
            }
            // 小于或等于 搜索条件:申请时间截止
            if (createTimeEnd != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("createTime"),
                        createTimeEnd));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

}
