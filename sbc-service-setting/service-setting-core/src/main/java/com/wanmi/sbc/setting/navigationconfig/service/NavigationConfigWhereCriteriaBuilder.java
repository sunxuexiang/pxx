package com.wanmi.sbc.setting.navigationconfig.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigQueryRequest;
import com.wanmi.sbc.setting.navigationconfig.model.root.NavigationConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>导航配置动态查询条件构建器</p>
 * @author lvheng
 * @date 2021-04-12 14:46:18
 */
public class NavigationConfigWhereCriteriaBuilder {
    public static Specification<NavigationConfig> build(NavigationConfigQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-idList
            if (CollectionUtils.isNotEmpty(queryRequest.getIdList())) {
                predicates.add(root.get("id").in(queryRequest.getIdList()));
            }

            // id
            if (queryRequest.getId() != null) {
                predicates.add(cbuild.equal(root.get("id"), queryRequest.getId()));
            }

            // 模糊查询 - 导航名称
            if (StringUtils.isNotEmpty(queryRequest.getNavName())) {
                predicates.add(cbuild.like(root.get("navName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getNavName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 导航图标-未点击状态
            if (StringUtils.isNotEmpty(queryRequest.getIconShow())) {
                predicates.add(cbuild.like(root.get("iconShow"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getIconShow()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 导航图标-点击状态
            if (StringUtils.isNotEmpty(queryRequest.getIconClick())) {
                predicates.add(cbuild.like(root.get("iconClick"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getIconClick()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
