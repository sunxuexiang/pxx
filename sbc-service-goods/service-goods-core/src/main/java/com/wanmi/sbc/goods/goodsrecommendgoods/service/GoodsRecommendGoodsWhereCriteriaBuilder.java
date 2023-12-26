package com.wanmi.sbc.goods.goodsrecommendgoods.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsQueryRequest;
import com.wanmi.sbc.goods.goodsrecommendgoods.model.root.GoodsRecommendGoods;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>商品推荐商品动态查询条件构建器</p>
 * @author chenyufei
 * @date 2019-09-07 10:53:36
 */
public class GoodsRecommendGoodsWhereCriteriaBuilder {
    public static Specification<GoodsRecommendGoods> build(GoodsRecommendGoodsQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-推荐商品主键编号List
            if (CollectionUtils.isNotEmpty(queryRequest.getRecommendIdList())) {
                predicates.add(root.get("recommendId").in(queryRequest.getRecommendIdList()));
            }

            // 批量查询-推荐商品主键编号List
            if (CollectionUtils.isNotEmpty(queryRequest.getGoodsInfoIds())) {
                predicates.add(root.get("goodsInfoId").in(queryRequest.getGoodsInfoIds()));
            }

            // 推荐商品主键编号
            if (StringUtils.isNotEmpty(queryRequest.getRecommendId())) {
                predicates.add(cbuild.equal(root.get("recommendId"), queryRequest.getRecommendId()));
            }

            // 模糊查询 - 推荐的商品编号
            if (StringUtils.isNotEmpty(queryRequest.getGoodsInfoId())) {
                predicates.add(cbuild.like(root.get("goodsInfoId"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getGoodsInfoId()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
