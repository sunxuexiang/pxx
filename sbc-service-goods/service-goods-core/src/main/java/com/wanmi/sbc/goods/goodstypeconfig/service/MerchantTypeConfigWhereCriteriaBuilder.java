package com.wanmi.sbc.goods.goodstypeconfig.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsQueryRequest;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantTypeConfigQueryRequest;
import com.wanmi.sbc.goods.goodstypeconfig.root.MerchantRecommendType;
import com.wanmi.sbc.goods.goodstypeconfig.root.RecommendType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>分类推荐分类动态查询条件构建器</p>
 * @author sgy
 * @date 2023-06-07 10:53:36
 */
public class MerchantTypeConfigWhereCriteriaBuilder {
    public static Specification<MerchantRecommendType> build(MerchantTypeConfigQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-推荐分类主键编号List
            if (CollectionUtils.isNotEmpty(queryRequest.getMerchantRecommendTypeIdList())) {
                predicates.add(root.get("merchantRecommendTypeId").in(queryRequest.getMerchantRecommendTypeIdList()));
            }

            // 批量查询-推荐分类主键编号List
            if (CollectionUtils.isNotEmpty(queryRequest.getMerchantRecommendTypeIds())) {
                predicates.add(root.get("merchantRecommendType").in(queryRequest.getMerchantRecommendTypeIds()));
            }

            // 推荐分类主键编号
            if (StringUtils.isNotEmpty(queryRequest.getMerchantRecommendTypeId())) {
                predicates.add(cbuild.equal(root.get("merchantRecommendTypeId"), queryRequest.getMerchantRecommendTypeId()));
            }
            if (queryRequest.getCompanyInfoId() != null) {
                predicates.add(cbuild.equal(root.get("companyInfoId"), queryRequest.getCompanyInfoId()));
            }

            // 店铺id
            if (queryRequest.getStoreId() != null) {
                predicates.add(cbuild.equal(root.get("storeId"), queryRequest.getStoreId()));
            }
            // 模糊查询 - 推荐的分类编号
            if (StringUtils.isNotEmpty(queryRequest.getMerchantRecommendTypeId())) {
                predicates.add(cbuild.like(root.get("merchantRecommendType"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getMerchantRecommendTypeId()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

    public static Specification<RecommendType> storeBuild(MerchantTypeConfigQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-推荐分类主键编号List
            if (CollectionUtils.isNotEmpty(queryRequest.getMerchantRecommendTypeIdList())) {
                predicates.add(root.get("merchantRecommendTypeId").in(queryRequest.getMerchantRecommendTypeIdList()));
            }

            // 批量查询-推荐分类主键编号List
            if (CollectionUtils.isNotEmpty(queryRequest.getMerchantRecommendTypeIds())) {
                predicates.add(root.get("merchantRecommendType").in(queryRequest.getMerchantRecommendTypeIds()));
            }

            // 推荐分类主键编号
            if (StringUtils.isNotEmpty(queryRequest.getMerchantRecommendTypeId())) {
                predicates.add(cbuild.equal(root.get("merchantRecommendTypeId"), queryRequest.getMerchantRecommendTypeId()));
            }
            if (queryRequest.getCompanyInfoId() != null) {
                predicates.add(cbuild.equal(root.get("companyInfoId"), queryRequest.getCompanyInfoId()));
            }

            // 店铺id
            if (queryRequest.getStoreId() != null) {
                predicates.add(cbuild.equal(root.get("storeId"), queryRequest.getStoreId()));
            }
            // 模糊查询 - 推荐的分类编号
            if (StringUtils.isNotEmpty(queryRequest.getMerchantRecommendTypeId())) {
                predicates.add(cbuild.like(root.get("merchantRecommendType"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getMerchantRecommendTypeId()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
