package com.wanmi.sbc.goods.warehousecity.service;

import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityQueryRequest;
import com.wanmi.sbc.goods.warehousecity.model.root.WareHouseCity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p> 仓库地区表动态查询条件构建器</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:28:33
 */
public class WareHouseCityWhereCriteriaBuilder {
    public static Specification<WareHouseCity> build(WareHouseCityQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-主键IDList
            if (CollectionUtils.isNotEmpty(queryRequest.getIdList())) {
                predicates.add(root.get("id").in(queryRequest.getIdList()));
            }

            // 主键ID
            if (queryRequest.getId() != null) {
                predicates.add(cbuild.equal(root.get("id"), queryRequest.getId()));
            }

            // 仓库iD
            if (queryRequest.getWareId() != null) {
                predicates.add(cbuild.equal(root.get("wareId"), queryRequest.getWareId()));
            }

            // 省份
            if (queryRequest.getProvinceId() != null) {
                predicates.add(cbuild.equal(root.get("provinceId"), queryRequest.getProvinceId()));
            }

            // 市
            if (queryRequest.getCityId() != null) {
                predicates.add(cbuild.equal(root.get("cityId"), queryRequest.getCityId()));
            }

            // 区县ID
            if (queryRequest.getAreaId() != null) {
                predicates.add(cbuild.equal(root.get("areaId"), queryRequest.getAreaId()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
