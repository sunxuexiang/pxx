package com.wanmi.sbc.goods.warehouse.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseQueryRequest;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>仓库表动态查询条件构建器</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
public class WareHouseWhereCriteriaBuilder {
    public static Specification<WareHouse> build(WareHouseQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-wareIdList
            if (CollectionUtils.isNotEmpty(queryRequest.getWareIdList())) {
                predicates.add(root.get("wareId").in(queryRequest.getWareIdList()));
            }

            // 批量查询-wareCodeList
            if (CollectionUtils.isNotEmpty(queryRequest.getWareCodeList())) {
                predicates.add(root.get("wareCode").in(queryRequest.getWareCodeList()));
            }

            // wareId
            if (queryRequest.getWareId() != null) {
                predicates.add(cbuild.equal(root.get("wareId"), queryRequest.getWareId()));
            }
            if (CollectionUtils.isNotEmpty(queryRequest.getStoreIdList())){
                predicates.add(root.get("storeId").in(queryRequest.getStoreIdList()));
            }

            // 店铺标识
            if (queryRequest.getStoreId() != null) {
                predicates.add(cbuild.equal(root.get("storeId"), queryRequest.getStoreId()));
            }

            // 模糊查询 - 仓库名称
            if (StringUtils.isNotEmpty(queryRequest.getWareName())) {
                predicates.add(cbuild.like(root.get("wareName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getWareName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 仓库编号
            if (StringUtils.isNotEmpty(queryRequest.getWareCode())) {
                predicates.add(cbuild.like(root.get("wareCode"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getWareCode()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 省份
            if (queryRequest.getProvinceId() != null) {
                predicates.add(cbuild.equal(root.get("provinceId"), queryRequest.getProvinceId()));
            }

            // 市
            if (queryRequest.getCityId() != null) {
                predicates.add(cbuild.equal(root.get("cityId"), queryRequest.getCityId()));
            }

            // 区
            if (queryRequest.getAreaId() != null) {
                predicates.add(cbuild.equal(root.get("areaId"), queryRequest.getAreaId()));
            }

            // 模糊查询 - 详细地址
            if (StringUtils.isNotEmpty(queryRequest.getAddressDetail())) {
                predicates.add(cbuild.like(root.get("addressDetail"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getAddressDetail()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 是否默认仓 0：否，1：是
            if (queryRequest.getDefaultFlag() != null) {
                predicates.add(cbuild.equal(root.get("defaultFlag"), queryRequest.getDefaultFlag()));
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

            // 模糊查询 - 编辑人
            if (StringUtils.isNotEmpty(queryRequest.getUpdatePerson())) {
                predicates.add(cbuild.like(root.get("updatePerson"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getUpdatePerson()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 是否删除标志 0：否，1：是
            if (queryRequest.getDelFlag() != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }

            // 是否自提
            if (queryRequest.getPickUpFlag() != null) {
                predicates.add(cbuild.equal(root.get("pickUpFlag"), queryRequest.getPickUpFlag()));
            }

            // 0:线下仓 1：线上仓
            if (queryRequest.getType() !=null){
                predicates.add(cbuild.equal(root.get("type"), queryRequest.getType()));
            }

            if (queryRequest.getWareHouseType()!=null){
                predicates.add(cbuild.equal(root.get("wareHouseType"), queryRequest.getWareHouseType()));
            }

            //创建排序
//            cquery.orderBy(cbuild.desc(root.get("defaultFlag")));
//            cquery.orderBy(cbuild.desc(root.get("createTime")));

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
