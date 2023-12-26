package com.wanmi.sbc.goods.goodsrecommendsetting.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.goods.api.request.goodsrecommendsetting.GoodsRecommendSettingQueryRequest;
import com.wanmi.sbc.goods.goodsrecommendsetting.model.root.GoodsRecommendSetting;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>商品推荐配置动态查询条件构建器</p>
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
public class GoodsRecommendSettingWhereCriteriaBuilder {
    public static Specification<GoodsRecommendSetting> build(GoodsRecommendSettingQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-商品推荐配置主键List
            if (CollectionUtils.isNotEmpty(queryRequest.getSettingIdList())) {
                predicates.add(root.get("settingId").in(queryRequest.getSettingIdList()));
            }

            // 商品推荐配置主键
            if (StringUtils.isNotEmpty(queryRequest.getSettingId())) {
                predicates.add(cbuild.equal(root.get("settingId"), queryRequest.getSettingId()));
            }

            // 商品推荐开关 （0:开启；1:关闭）
            if (queryRequest.getEnabled() != null) {
                predicates.add(cbuild.equal(root.get("enabled"), queryRequest.getEnabled()));
            }

            // 模糊查询 - 推荐入口
            if (StringUtils.isNotEmpty(queryRequest.getEntries())) {
                predicates.add(cbuild.like(root.get("entries"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getEntries()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 优先级
            if (queryRequest.getPriority() != null) {
                predicates.add(cbuild.equal(root.get("priority"), queryRequest.getPriority()));
            }

            // 推荐规则
            if (queryRequest.getRule() != null) {
                predicates.add(cbuild.equal(root.get("rule"), queryRequest.getRule()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
