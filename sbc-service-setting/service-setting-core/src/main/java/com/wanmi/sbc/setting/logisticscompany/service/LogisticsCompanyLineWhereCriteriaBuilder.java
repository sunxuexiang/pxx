package com.wanmi.sbc.setting.logisticscompany.service;

import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyLineQueryRequest;
import com.wanmi.sbc.setting.logisticscompany.model.root.LogisticsCompanyLine;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc  物流线路
 * @author shiy  2023/11/7 9:29
*/
public class LogisticsCompanyLineWhereCriteriaBuilder {
    public static Specification<LogisticsCompanyLine> build(LogisticsCompanyLineQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            boolean isFirstQuery =false;
            List<Predicate> predicates = new ArrayList<>();
            // id
            if (queryRequest.getLineId() != null) {
                predicates.add(cbuild.equal(root.get("lineId"), queryRequest.getLineId()));
                isFirstQuery = true;
            }

            // 删除标志
            if (queryRequest.getDelFlag() != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }

            if(!isFirstQuery) {
                if (queryRequest.getLogisticsId() != null) {
                    predicates.add(cbuild.equal(root.get("logisticsId"), queryRequest.getLogisticsId()));
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
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
