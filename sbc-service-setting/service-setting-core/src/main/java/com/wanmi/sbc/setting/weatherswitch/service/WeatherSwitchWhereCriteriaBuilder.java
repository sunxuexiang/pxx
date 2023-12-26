package com.wanmi.sbc.setting.weatherswitch.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.setting.api.request.weatherswitch.WeatherSwitchQueryRequest;
import com.wanmi.sbc.setting.weatherswitch.model.root.WeatherSwitch;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>天气设置动态查询条件构建器</p>
 * @author 费传奇
 * @date 2021-04-16 09:54:37
 */
public class WeatherSwitchWhereCriteriaBuilder {
    public static Specification<WeatherSwitch> build(WeatherSwitchQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-开关idList
            if (CollectionUtils.isNotEmpty(queryRequest.getIdList())) {
                predicates.add(root.get("id").in(queryRequest.getIdList()));
            }

            // 开关id
            if (queryRequest.getId() != null) {
                predicates.add(cbuild.equal(root.get("id"), queryRequest.getId()));
            }

            // 顶部背景图状态(0.关闭，1开启)
            if (queryRequest.getTopImgStatus() != null) {
                predicates.add(cbuild.equal(root.get("topImgStatus"), queryRequest.getTopImgStatus()));
            }

            // 模糊查询 - 顶部背景图
            if (StringUtils.isNotEmpty(queryRequest.getTopImg())) {
                predicates.add(cbuild.like(root.get("topImg"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getTopImg()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // slogan图图状态(0.关闭，1开启)
            if (queryRequest.getSloganImgStatus() != null) {
                predicates.add(cbuild.equal(root.get("sloganImgStatus"), queryRequest.getSloganImgStatus()));
            }

            // 模糊查询 - slogan图
            if (StringUtils.isNotEmpty(queryRequest.getSloganImg())) {
                predicates.add(cbuild.like(root.get("sloganImg"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getSloganImg()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 组件开关状态 (0：关闭 1：开启)
            if (queryRequest.getComponentStatus() != null) {
                predicates.add(cbuild.equal(root.get("componentStatus"), queryRequest.getComponentStatus()));
            }

            // 是否设置 (0：关闭 1：开启)
            if (queryRequest.getSearchBackStatus() != null) {
                predicates.add(cbuild.equal(root.get("searchBackStatus"), queryRequest.getSearchBackStatus()));
            }

            // 模糊查询 - 搜索背景色
            if (StringUtils.isNotEmpty(queryRequest.getSearchBackColor())) {
                predicates.add(cbuild.like(root.get("searchBackColor"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getSearchBackColor()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
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

            // 大于或等于 搜索条件:修改时间开始
            if (queryRequest.getUpdateTimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("updateTime"),
                        queryRequest.getUpdateTimeBegin()));
            }
            // 小于或等于 搜索条件:修改时间截止
            if (queryRequest.getUpdateTimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("updateTime"),
                        queryRequest.getUpdateTimeEnd()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
