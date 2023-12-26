package com.wanmi.sbc.goods.icitem.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.goods.api.request.icitem.IcitemQueryRequest;
import com.wanmi.sbc.goods.icitem.model.root.Icitem;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.apache.commons.lang3.StringUtils;
import com.wanmi.sbc.common.util.XssUtils;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>配送到家动态查询条件构建器</p>
 * @author lh
 * @date 2020-12-05 18:16:34
 */
public class IcitemWhereCriteriaBuilder {
    public static Specification<Icitem> build(IcitemQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-skuList
            if (CollectionUtils.isNotEmpty(queryRequest.getSkuList())) {
                predicates.add(root.get("sku").in(queryRequest.getSkuList()));
            }

            // sku
            if (StringUtils.isNotEmpty(queryRequest.getSku())) {
                predicates.add(cbuild.equal(root.get("sku"), queryRequest.getSku()));
            }

            // 模糊查询 - name
            if (StringUtils.isNotEmpty(queryRequest.getName())) {
                predicates.add(cbuild.like(root.get("name"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // tiji
            if (queryRequest.getTiji() != null) {
                predicates.add(cbuild.equal(root.get("tiji"), queryRequest.getTiji()));
            }

            // weight
            if (queryRequest.getWeight() != null) {
                predicates.add(cbuild.equal(root.get("weight"), queryRequest.getWeight()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
