package com.wanmi.sbc.setting.logisticscompany.service;

import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyQueryRequest;
import com.wanmi.sbc.setting.logisticscompany.model.root.LogisticsCompany;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>配送到家动态查询条件构建器</p>
 * @author fcq
 * @date 2020-11-06 13:37:51
 */
public class LogisticsCompanyWhereCriteriaBuilder {
    public static Specification<LogisticsCompany> build(LogisticsCompanyQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            boolean isFirstQuery =false;
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-idList
            if (CollectionUtils.isNotEmpty(queryRequest.getIdList())) {
                predicates.add(root.get("id").in(queryRequest.getIdList()));
                isFirstQuery = true;
            }

            // id
            if (queryRequest.getId() != null) {
                predicates.add(cbuild.equal(root.get("id"), queryRequest.getId()));
                isFirstQuery = true;
            }

            // 删除标志
            if (queryRequest.getDelFlag() != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }

            if(!isFirstQuery) {
                // 模糊查询 - 公司编号
                if (StringUtils.isNotEmpty(queryRequest.getCompanyNumber())) {
                    predicates.add(cbuild.like(root.get("companyNumber"), StringUtil.SQL_LIKE_CHAR
                            .concat(XssUtils.replaceLikeWildcard(queryRequest.getCompanyNumber()))
                            .concat(StringUtil.SQL_LIKE_CHAR)));
                }

                // 模糊查询 - 公司名称
                if (StringUtils.isNotEmpty(queryRequest.getLogisticsName())) {
                    predicates.add(cbuild.like(root.get("logisticsName"), StringUtil.SQL_LIKE_CHAR
                            .concat(XssUtils.replaceLikeWildcard(queryRequest.getLogisticsName()))
                            .concat(StringUtil.SQL_LIKE_CHAR)));
                }

                // 模糊查询 - 公司电话
                if (StringUtils.isNotEmpty(queryRequest.getLogisticsPhone())) {
                    predicates.add(cbuild.like(root.get("logisticsPhone"), StringUtil.SQL_LIKE_CHAR
                            .concat(XssUtils.replaceLikeWildcard(queryRequest.getLogisticsPhone()))
                            .concat(StringUtil.SQL_LIKE_CHAR)));
                }

                // 模糊查询 - 物流公司地址
                if (StringUtils.isNotEmpty(queryRequest.getLogisticsAddress())) {
                    predicates.add(cbuild.like(root.get("logisticsAddress"), StringUtil.SQL_LIKE_CHAR
                            .concat(XssUtils.replaceLikeWildcard(queryRequest.getLogisticsAddress()))
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

                if (queryRequest.getLogisticsType() != null) {
                    predicates.add(cbuild.equal(root.get("logisticsType"), queryRequest.getLogisticsType()));
                }
                //店铺ID
                if (Objects.isNull(queryRequest.getStoreId())) {
                    if (queryRequest.getMarketId() != null) {
                        predicates.add(cbuild.equal(root.get("marketId"), queryRequest.getMarketId()));
                    }
                    if(StringUtils.isNotBlank(queryRequest.getCreatePerson())){
                        predicates.add(cbuild.equal(root.get("createPerson"), queryRequest.getCreatePerson()));
                    }
                } else {
                    if (queryRequest.getMarketId() != null) {
                        List<Predicate> orpredicates = new ArrayList<>();
                        orpredicates.add(cbuild.equal(root.get("storeId"), queryRequest.getStoreId()));
                        orpredicates.add(cbuild.equal(root.get("marketId"), queryRequest.getMarketId()));
                        if(StringUtils.isNotBlank(queryRequest.getCreatePerson())){
                            orpredicates.add(cbuild.equal(root.get("createPerson"), queryRequest.getCreatePerson()));
                        }
                        Predicate[] p2 = orpredicates.toArray(new Predicate[orpredicates.size()]);
                        predicates.add(cbuild.or(p2));
                    } else {
                        List<Predicate> orpredicates = new ArrayList<>();
                        orpredicates.add(cbuild.equal(root.get("storeId"), queryRequest.getStoreId()));
                        if(StringUtils.isNotBlank(queryRequest.getCreatePerson())){
                            orpredicates.add(cbuild.equal(root.get("createPerson"), queryRequest.getCreatePerson()));
                        }
                        Predicate[] p2 = orpredicates.toArray(new Predicate[orpredicates.size()]);
                        predicates.add(cbuild.or(p2));
                    }
                }
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
