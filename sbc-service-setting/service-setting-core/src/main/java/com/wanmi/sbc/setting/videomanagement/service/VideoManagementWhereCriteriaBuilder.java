package com.wanmi.sbc.setting.videomanagement.service;

import com.wanmi.sbc.common.util.StringUtil;

import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.setting.api.request.videomanagement.VideoManagementQueryRequest;
import com.wanmi.sbc.setting.videomanagement.model.root.VideoLike;
import com.wanmi.sbc.setting.videomanagement.model.root.VideoManagement;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>视频管理动态查询条件构建器</p>
 * @author zhaowei
 * @date 2021-04-17 17:47:22
 */
public class VideoManagementWhereCriteriaBuilder {
    public static Specification<VideoManagement> build(VideoManagementQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<Predicate> orPredicates = new ArrayList<>();

            // 批量查询-IDList
            if (CollectionUtils.isNotEmpty(queryRequest.getVideoIdList())) {
                predicates.add(root.get("videoId").in(queryRequest.getVideoIdList()));
            }

            // ID
            if (queryRequest.getVideoId() != null) {
                predicates.add(cbuild.equal(root.get("videoId"), queryRequest.getVideoId()));
            }

            // 状态0:上架,1:下架
            if (queryRequest.getState() != null) {
                predicates.add(cbuild.equal(root.get("state"), queryRequest.getState()));
            }

            // 播放数
            if (queryRequest.getPlayFew() != null) {
                predicates.add(cbuild.equal(root.get("playFew"), queryRequest.getPlayFew()));
            }

            // 模糊查询 - 素材KEY
            if (StringUtils.isNotEmpty(queryRequest.getResourceKey())) {
                predicates.add(cbuild.like(root.get("resourceKey"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getResourceKey()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 素材地址
            if (StringUtils.isNotEmpty(queryRequest.getArtworkUrl())) {
                predicates.add(cbuild.like(root.get("artworkUrl"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getArtworkUrl()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 大于或等于 搜索条件:发布时间开始
            if (queryRequest.getCreateTimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("createTime"),
                        queryRequest.getCreateTimeBegin()));
            }
            // 小于或等于 搜索条件:发布时间截止
            if (queryRequest.getCreateTimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("createTime"),
                        queryRequest.getCreateTimeEnd()));
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

            // 删除标识,0:未删除1:已删除
            if (queryRequest.getDelFlag() != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }

            // 模糊查询 - oss服务器类型，对应system_config的config_type
            if (StringUtils.isNotEmpty(queryRequest.getServerType())) {
                predicates.add(cbuild.like(root.get("serverType"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getServerType()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            //根据上传用户id查询
            if(CollectionUtils.isNotEmpty(queryRequest.getCoverFollowCustomerId())){
                CriteriaBuilder.In<Object> in = cbuild.in(root.get("coverFollowCustomerId"));
                List<String> customerIds = queryRequest.getCoverFollowCustomerId();
                for (String customerId: customerIds) {
                    in.value(customerId);
                }
                predicates.add(in);
            }

            if (queryRequest.getStoreId() != null && queryRequest.getStoreId() > 0) {
                predicates.add(cbuild.equal(root.get("storeId"), queryRequest.getStoreId()));
            }

            boolean andStoreIdList = true;

            if (queryRequest.getSearchType() != null) {
                if (queryRequest.getSearchType().equals(0)) {
                    if (!ObjectUtils.isEmpty(queryRequest.getStoreIdList())) {
                        orPredicates.add(cbuild.like(root.get("videoName"), StringUtil.SQL_LIKE_CHAR
                                .concat(XssUtils.replaceLikeWildcard(queryRequest.getVideoName()))
                                .concat(StringUtil.SQL_LIKE_CHAR)));

                        CriteriaBuilder.In in = cbuild.in(root.get("storeId"));
                        queryRequest.getStoreIdList().forEach(id -> {
                            in.value(id);
                        });
                        orPredicates.add(in);

                        Predicate predicateOr = cbuild.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
                        predicates.add(predicateOr);
                        andStoreIdList = false;
                    }
                    else {
                        predicates.add(cbuild.like(root.get("videoName"), StringUtil.SQL_LIKE_CHAR
                                .concat(XssUtils.replaceLikeWildcard(queryRequest.getVideoName()))
                                .concat(StringUtil.SQL_LIKE_CHAR)));
                    }
                }
                else if (queryRequest.getSearchType().equals(1)) {
                    CriteriaBuilder.In in = cbuild.in(root.get("storeId"));
                    queryRequest.getStoreIdList().forEach(id -> {
                        in.value(id);
                    });
                    predicates.add(in);
                }
                else if (queryRequest.getSearchType().equals(2)) {
                    andStoreIdList = false;
                    predicates.add(cbuild.like(root.get("videoName"), StringUtil.SQL_LIKE_CHAR
                            .concat(XssUtils.replaceLikeWildcard(queryRequest.getVideoName()))
                            .concat(StringUtil.SQL_LIKE_CHAR)));
                }
            }
            else {
                // 模糊查询 - 视频名称
                if (StringUtils.isNotEmpty(queryRequest.getVideoName())) {
                    predicates.add(cbuild.like(root.get("videoName"), StringUtil.SQL_LIKE_CHAR
                            .concat(XssUtils.replaceLikeWildcard(queryRequest.getVideoName()))
                            .concat(StringUtil.SQL_LIKE_CHAR)));
                }
            }

            if (andStoreIdList && CollectionUtils.isNotEmpty(queryRequest.getStoreIdList())) {
                CriteriaBuilder.In<Object> in = cbuild.in(root.get("storeId"));
                for (Long store : queryRequest.getStoreIdList()) {
                    in.value(store);
                }
                predicates.add(in);
            }

            if (StringUtils.isNotEmpty(queryRequest.getSortColumn())) {
                cquery.orderBy(cbuild.desc(root.get(queryRequest.getSortColumn())));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
