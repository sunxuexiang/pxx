package com.wanmi.sbc.goods.catebrandsortrel.service;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelQueryRequest;
import com.wanmi.sbc.goods.catebrandsortrel.model.root.CateBrandSortRel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>类目品牌排序表动态查询条件构建器</p>
 * @author lvheng
 * @date 2021-04-08 11:24:32
 */
public class CateBrandSortRelWhereCriteriaBuilder {
    public static Specification<CateBrandSortRel> build(CateBrandSortRelQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-品类IDList
            if (CollectionUtils.isNotEmpty(queryRequest.getCateIdList())) {
                predicates.add(root.get("cateId").in(queryRequest.getCateIdList()));
            }

            // 批量查询-品牌ID
            if (CollectionUtils.isNotEmpty(queryRequest.getBrandIdList())) {
                predicates.add(root.get("brandId").in(queryRequest.getBrandIdList()));
            }

            // 品类ID
            if (queryRequest.getCateId() != null) {
                predicates.add(cbuild.equal(root.get("cateId"), queryRequest.getCateId()));
            }

            // 模糊查询 - 品牌名称
            if (StringUtils.isNotEmpty(queryRequest.getName())) {
                predicates.add(cbuild.like(root.get("name"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }


            // 排序序号
            if (queryRequest.getSerialNo() != null) {
                predicates.add(cbuild.equal(root.get("serialNo"), queryRequest.getSerialNo()));
            }

            // 删除标识，0：未删除 1：已删除
            if (queryRequest.getDelFlag() != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
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

            // 模糊查询 - 创建人
            if (StringUtils.isNotEmpty(queryRequest.getCreatePerson())) {
                predicates.add(cbuild.like(root.get("createPerson"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getCreatePerson()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 大于或等于 搜索条件:更新时间开始
            if (queryRequest.getUpdateTimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("updateTime"),
                        queryRequest.getUpdateTimeBegin()));
            }
            // 小于或等于 搜索条件:更新时间截止
            if (queryRequest.getUpdateTimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("updateTime"),
                        queryRequest.getUpdateTimeEnd()));
            }

            // 模糊查询 - 更新人
            if (StringUtils.isNotEmpty(queryRequest.getUpdatePerson())) {
                predicates.add(cbuild.like(root.get("updatePerson"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getUpdatePerson()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            if (Objects.nonNull(queryRequest.getSortStatus())) {
                if (DefaultFlag.YES.equals(queryRequest.getSortStatus())) {
                    //已排序
                    predicates.add(cbuild.isNotNull(root.get("serialNo")));
                }else {
                    //未排序
                    predicates.add(cbuild.isNull(root.get("serialNo")));
                }
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
