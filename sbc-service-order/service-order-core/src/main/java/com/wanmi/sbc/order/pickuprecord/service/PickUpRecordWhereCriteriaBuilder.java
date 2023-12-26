package com.wanmi.sbc.order.pickuprecord.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.order.api.request.pickuprecord.PickUpRecordQueryRequest;
import com.wanmi.sbc.order.pickuprecord.model.root.PickUpRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.apache.commons.lang3.StringUtils;
import com.wanmi.sbc.common.util.XssUtils;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>测试代码生成动态查询条件构建器</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
public class PickUpRecordWhereCriteriaBuilder {
    public static Specification<PickUpRecord> build(PickUpRecordQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-主键List
            if (CollectionUtils.isNotEmpty(queryRequest.getPickUpIdList())) {
                predicates.add(root.get("pickUpId").in(queryRequest.getPickUpIdList()));
            }

            // 主键
            if (StringUtils.isNotEmpty(queryRequest.getPickUpId())) {
                predicates.add(cbuild.equal(root.get("pickUpId"), queryRequest.getPickUpId()));
            }

            // 店铺id
            if (queryRequest.getStoreId() != null) {
                predicates.add(cbuild.equal(root.get("sotreId"), queryRequest.getStoreId()));
            }

            // 模糊查询 - 订单id
            if (StringUtils.isNotEmpty(queryRequest.getTradeId())) {
                predicates.add(cbuild.like(root.get("tradeId"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getTradeId()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 自提码
            if (queryRequest.getPickUpCode() != null) {
                predicates.add(cbuild.equal(root.get("pickUpCode"), queryRequest.getPickUpCode()));
            }

            // 是否已自提:0:未自提 1：已自提
            if (queryRequest.getPickUpFlag() != null) {
                predicates.add(cbuild.equal(root.get("pickUpFlag"), queryRequest.getPickUpFlag()));
            }

            // 大于或等于 搜索条件:自提时间开始
            if (queryRequest.getPickUpTimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("pickUpTime"),
                        queryRequest.getPickUpTimeBegin()));
            }
            // 小于或等于 搜索条件:自提时间截止
            if (queryRequest.getPickUpTimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("pickUpTime"),
                        queryRequest.getPickUpTimeEnd()));
            }

            // 删除标志位:0:未删除1：以上处
            if (queryRequest.getDelFlag() != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }

            // 大于或等于 搜索条件:创建表时间开始
            if (queryRequest.getCreateTimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("createTime"),
                        queryRequest.getCreateTimeBegin()));
            }
            // 小于或等于 搜索条件:创建表时间截止
            if (queryRequest.getCreateTimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("createTime"),
                        queryRequest.getCreateTimeEnd()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
