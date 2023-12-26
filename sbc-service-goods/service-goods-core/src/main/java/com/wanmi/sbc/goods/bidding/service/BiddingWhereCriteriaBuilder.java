package com.wanmi.sbc.goods.bidding.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.goods.api.request.bidding.BiddingQueryRequest;
import com.wanmi.sbc.goods.bean.enums.ActivityStatus;
import com.wanmi.sbc.goods.bidding.model.root.Bidding;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.apache.commons.lang3.StringUtils;
import com.wanmi.sbc.common.util.XssUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>竞价配置动态查询条件构建器</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
public class BiddingWhereCriteriaBuilder {
    public static Specification<Bidding> build(BiddingQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-竞价配置主键List
            if (CollectionUtils.isNotEmpty(queryRequest.getBiddingIdList())) {
                predicates.add(root.get("biddingId").in(queryRequest.getBiddingIdList()));
            }

            // 竞价配置主键
            if (StringUtils.isNotEmpty(queryRequest.getBiddingId())) {
                predicates.add(cbuild.equal(root.get("biddingId"), queryRequest.getBiddingId()));
            }

            // 模糊查询 - 关键字,分类
            if (StringUtils.isNotEmpty(queryRequest.getKeywords())) {
                predicates.add(cbuild.like(root.get("keywords"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getKeywords()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 竞价类型0:关键字，1:分类
            if (queryRequest.getBiddingType() != null) {
                predicates.add(cbuild.equal(root.get("biddingType"), queryRequest.getBiddingType().toValue()));
            }

            // 竞价的状态：0:未开始，1:进行中，2:已结束
            if (queryRequest.getBiddingStatus() != null) {
                predicates.add(cbuild.equal(root.get("biddingStatus"), queryRequest.getBiddingStatus().toValue()));
            }

            // 大于或等于 搜索条件:开始时间开始
            if (queryRequest.getStartTimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("startTime"),
                        queryRequest.getStartTimeBegin()));
            }
            // 小于或等于 搜索条件:开始时间截止
            if (queryRequest.getStartTimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("startTime"),
                        queryRequest.getStartTimeEnd()));
            }

            // 大于或等于 搜索条件:修改时间开始
            if (queryRequest.getModifyTimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("modifyTime"),
                        queryRequest.getModifyTimeBegin()));
            }
            // 小于或等于 搜索条件:修改时间截止
            if (queryRequest.getModifyTimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("modifyTime"),
                        queryRequest.getModifyTimeEnd()));
            }

            // 大于或等于 搜索条件:结束时间开始
            if (queryRequest.getEndTimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("endTime"),
                        queryRequest.getEndTimeBegin()));
            }
            // 小于或等于 搜索条件:结束时间截止
            if (queryRequest.getEndTimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("endTime"),
                        queryRequest.getEndTimeEnd()));
            }

            // 删除标志位
            if (queryRequest.getDelFlag() != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }

            // 未结束的竞价活动
            if(Objects.nonNull(queryRequest.getNoCompleted()) && queryRequest.getNoCompleted()){
                predicates.add(cbuild.or(cbuild.equal(root.get("biddingStatus"), ActivityStatus.ABOUT_TO_START.toValue())
                        , cbuild.equal(root.get("biddingStatus"), ActivityStatus.SALE.toValue())));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
