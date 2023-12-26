package com.wanmi.sbc.goods.soldoutgoods.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsQueryRequest;
import com.wanmi.sbc.goods.soldoutgoods.model.root.SoldOutGoods;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>类目品牌排序表动态查询条件构建器</p>
 * @author lvheng
 * @date 2021-04-10 15:09:50
 */
public class SoldOutGoodsWhereCriteriaBuilder {
    public static Specification<SoldOutGoods> build(SoldOutGoodsQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-将要被下架的商品List
            if (CollectionUtils.isNotEmpty(queryRequest.getGoodsIdList())) {
                predicates.add(root.get("goodsId").in(queryRequest.getGoodsIdList()));
            }

            // 将要被下架的商品
            if (StringUtils.isNotEmpty(queryRequest.getGoodsId())) {
                predicates.add(cbuild.equal(root.get("goodsId"), queryRequest.getGoodsId()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
