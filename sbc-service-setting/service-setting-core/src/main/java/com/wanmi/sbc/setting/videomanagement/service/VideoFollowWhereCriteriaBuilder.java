package com.wanmi.sbc.setting.videomanagement.service;

import com.wanmi.sbc.setting.api.request.videomanagement.VideoFollowQueryRequest;
import com.wanmi.sbc.setting.api.request.videomanagement.VideoLikeQueryRequest;
import com.wanmi.sbc.setting.videomanagement.model.root.VideoFollow;
import com.wanmi.sbc.setting.videomanagement.model.root.VideoLike;
import com.wanmi.sbc.setting.videomanagement.model.root.VideoManagement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/4/20
 */
public class VideoFollowWhereCriteriaBuilder {


    public static Specification<VideoFollow> build(VideoFollowQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<VideoFollow,VideoManagement> videoFolloeJoin = root.join("videoManagement");
            //关联查询

            predicates.add(cbuild.equal(videoFolloeJoin.get("delFlag"), queryRequest.getDelFlag()));
            predicates.add(cbuild.equal(videoFolloeJoin.get("state"), queryRequest.getState()));


            if (StringUtils.isNotEmpty(queryRequest.getFollowCustomerId())) {
                predicates.add(cbuild.equal(root.get("followCustomerId"), queryRequest.getFollowCustomerId()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

}
