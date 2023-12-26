package com.wanmi.sbc.goods.warehouse.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseDetailRequest;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouseDetail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>仓库表动态查询条件构建器</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
public class WareHouseDetailWhereCriteriaBuilder {
    public static Specification<WareHouseDetail> build(WareHouseDetailRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();


            // wareId
            if (queryRequest.getWareId() != null) {
                predicates.add(cbuild.equal(root.get("wareId"), queryRequest.getWareId()));
            }

            // 模糊查询 - 仓库名称
            if (StringUtils.isNotEmpty(queryRequest.getWareName())) {
                predicates.add(cbuild.like(root.get("wareName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getWareName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 是否删除标志 0：否，1：是
            if (queryRequest.getDelFlag() != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
