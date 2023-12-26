package com.wanmi.sbc.wms.record.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.wms.api.request.record.RecordQueryRequest;
import com.wanmi.sbc.wms.record.model.root.Record;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>请求记录动态查询条件构建器</p>
 * @author baijz
 * @date 2020-05-06 19:23:45
 */
public class RecordWhereCriteriaBuilder {
    public static Specification<Record> build(RecordQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-记录主键List
            if (CollectionUtils.isNotEmpty(queryRequest.getRecordIdList())) {
                predicates.add(root.get("recordId").in(queryRequest.getRecordIdList()));
            }

            // 记录主键
            if (queryRequest.getRecordId() != null) {
                predicates.add(cbuild.equal(root.get("recordId"), queryRequest.getRecordId()));
            }

            // 请求类型
            if (queryRequest.getMethod() != null) {
                predicates.add(cbuild.equal(root.get("method"), queryRequest.getMethod()));
            }

            // 模糊查询 - 请求的地址
            if (StringUtils.isNotEmpty(queryRequest.getRequestUrl())) {
                predicates.add(cbuild.like(root.get("requestUrl"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getRequestUrl()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 请求的实体
            if (StringUtils.isNotEmpty(queryRequest.getRequestBody())) {
                predicates.add(cbuild.like(root.get("requestBody"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getRequestBody()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 返回的信息
            if (StringUtils.isNotEmpty(queryRequest.getResposeInfo())) {
                predicates.add(cbuild.like(root.get("resposeInfo"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getResposeInfo()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 返回的状态
            if (StringUtils.isNotEmpty(queryRequest.getStatus())) {
                predicates.add(cbuild.like(root.get("status"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getStatus()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 大于或等于 搜索条件:请求时间开始
            if (queryRequest.getCreateTimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("createTime"),
                        queryRequest.getCreateTimeBegin()));
            }
            // 小于或等于 搜索条件:请求时间截止
            if (queryRequest.getCreateTimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("createTime"),
                        queryRequest.getCreateTimeEnd()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
