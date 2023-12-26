package com.wanmi.sbc.setting.banneradmin.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminQueryRequest;
import com.wanmi.sbc.setting.banneradmin.model.root.BannerAdmin;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>轮播管理动态查询条件构建器</p>
 * @author 费传奇
 * @date 2020-12-08 11:44:38
 */
public class BannerAdminWhereCriteriaBuilder {
    public static Specification<BannerAdmin> build(BannerAdminQueryRequest queryRequest) {
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

            // 模糊查询 - 名称
            if (StringUtils.isNotEmpty(queryRequest.getBannerName())) {
                predicates.add(cbuild.like(root.get("bannerName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getBannerName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 一级类ID
            if (queryRequest.getOneCateId() != null) {
                predicates.add(cbuild.equal(root.get("oneCateId"), queryRequest.getOneCateId()));
            }

            // 模糊查询 - 一级分类名称
            if (StringUtils.isNotEmpty(queryRequest.getOneCateName())) {
                predicates.add(cbuild.like(root.get("oneCateName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getOneCateName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 排序号
            if (queryRequest.getBannerSort() != null) {
                predicates.add(cbuild.equal(root.get("bannerSort"), queryRequest.getBannerSort()));
            }

            // 模糊查询 - 添加链接
            if (StringUtils.isNotEmpty(queryRequest.getLink())) {
                predicates.add(cbuild.like(root.get("link"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getLink()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - banner图片
            if (StringUtils.isNotEmpty(queryRequest.getBannerImg())) {
                predicates.add(cbuild.like(root.get("bannerImg"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getBannerImg()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 状态(0.显示 1.隐藏)
            if (queryRequest.getIsShow() != null) {
                predicates.add(cbuild.equal(root.get("isShow"), queryRequest.getIsShow()));
            }

            // 删除标志
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

            // 模糊查询 - 修改人
            if (StringUtils.isNotEmpty(queryRequest.getUpdatePerson())) {
                predicates.add(cbuild.like(root.get("updatePerson"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getUpdatePerson()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 大于或等于 搜索条件:删除时间开始
            if (queryRequest.getDeleteTimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("deleteTime"),
                        queryRequest.getDeleteTimeBegin()));
            }
            // 小于或等于 搜索条件:删除时间截止
            if (queryRequest.getDeleteTimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("deleteTime"),
                        queryRequest.getDeleteTimeEnd()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
