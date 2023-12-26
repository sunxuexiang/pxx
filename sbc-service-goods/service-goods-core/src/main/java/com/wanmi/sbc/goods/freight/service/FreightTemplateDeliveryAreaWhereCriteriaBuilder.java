package com.wanmi.sbc.goods.freight.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.goods.freight.model.root.FreightTemplateDeliveryArea;
import com.wanmi.sbc.goods.freight.request.FreightTemplateDeliveryAreaQueryRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>配送到家范围动态查询条件构建器</p>
 * @author zhaowei
 * @date 2021-03-25 16:57:57
 */
public class FreightTemplateDeliveryAreaWhereCriteriaBuilder {
    public static Specification<FreightTemplateDeliveryArea> build(FreightTemplateDeliveryAreaQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-主键标识List
            if (CollectionUtils.isNotEmpty(queryRequest.getIdList())) {
                predicates.add(root.get("id").in(queryRequest.getIdList()));
            }

            // 主键标识
            if (queryRequest.getId() != null) {
                predicates.add(cbuild.equal(root.get("id"), queryRequest.getId()));
            }

            // 模糊查询 - 配送地id(逗号分隔)
            if (StringUtils.isNotEmpty(queryRequest.getDestinationArea())) {
                predicates.add(cbuild.like(root.get("destinationArea"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getDestinationArea()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 配送地名称(逗号分隔)
            if (StringUtils.isNotEmpty(queryRequest.getDestinationAreaName())) {
                predicates.add(cbuild.like(root.get("destinationAreaName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getDestinationAreaName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 店铺标识
            if (queryRequest.getStoreId() != null) {
                predicates.add(cbuild.equal(root.get("storeId"), queryRequest.getStoreId()));
            }

            // 公司信息ID
            if (queryRequest.getCompanyInfoId() != null) {
                predicates.add(cbuild.equal(root.get("companyInfoId"), queryRequest.getCompanyInfoId()));
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

            // 是否删除(0:否,1:是)
            if (queryRequest.getDelFlag() != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }

            // 仓库ID
            if (queryRequest.getWareId() != null) {
                predicates.add(cbuild.equal(root.get("wareId"), queryRequest.getWareId()));
            }
            // 免费店配类型
            if (queryRequest.getDestinationType() != null) {
                predicates.add(cbuild.equal(root.get("destinationType"), queryRequest.getDestinationType()));
            }

            if (queryRequest.getOpenFlag() != null) {
                predicates.add(cbuild.equal(root.get("openFlag"), queryRequest.getOpenFlag()));
            }

            // 批量查询-主键标识List
            if (CollectionUtils.isNotEmpty(queryRequest.getDestinationTypeList())) {
                predicates.add(root.get("destinationType").in(queryRequest.getDestinationTypeList()));
            }

            if (CollectionUtils.isNotEmpty(queryRequest.getStoreIdList())) {
                predicates.add(root.get("storeId").in(queryRequest.getStoreIdList()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
