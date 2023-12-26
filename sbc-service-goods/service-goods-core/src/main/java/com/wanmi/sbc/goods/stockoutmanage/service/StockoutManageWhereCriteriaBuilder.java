package com.wanmi.sbc.goods.stockoutmanage.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageQueryRequest;
import com.wanmi.sbc.goods.stockoutmanage.model.root.StockoutManage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>缺货管理动态查询条件构建器</p>
 *
 * @author tzx
 * @date 2020-05-27 16:38:24
 */
public class StockoutManageWhereCriteriaBuilder {
    public static Specification<StockoutManage> build(StockoutManageQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-缺货管理List
            if (CollectionUtils.isNotEmpty(queryRequest.getStockoutIdList())) {
                predicates.add(root.get("stockoutId").in(queryRequest.getStockoutIdList()));
            }

            // 缺货管理
            if (StringUtils.isNotEmpty(queryRequest.getStockoutId())) {
                predicates.add(cbuild.equal(root.get("stockoutId"), queryRequest.getStockoutId()));
            }

            if (StringUtils.isNotEmpty(queryRequest.getErpGoodsInfoNo())) {
                predicates.add(cbuild.equal(root.get("erpGoodsInfoNo"), queryRequest.getErpGoodsInfoNo()));
            }

            // 模糊查询 - 商品名称
            if (StringUtils.isNotEmpty(queryRequest.getGoodsName())) {
                predicates.add(cbuild.like(root.get("goodsName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getGoodsName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            if (queryRequest.getCateId() != null) {
                predicates.add(cbuild.equal(root.get("cateId"), queryRequest.getCateId()));
            }

            if (StringUtils.isNotEmpty(queryRequest.getCateName())) {
                predicates.add(cbuild.equal(root.get("cateName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getCateName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - sku id
            if (StringUtils.isNotEmpty(queryRequest.getGoodsInfoId())) {
                predicates.add(cbuild.like(root.get("goodsInfoId"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getGoodsInfoId()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - sku 编码
            if (StringUtils.isNotEmpty(queryRequest.getGoodsInfoNo())) {
                predicates.add(cbuild.like(root.get("goodsInfoNo"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getGoodsInfoNo()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 品牌id
            if (queryRequest.getBrandId() != null) {
                predicates.add(cbuild.equal(root.get("brandId"), queryRequest.getBrandId()));
            }

            // 模糊查询 - 品牌名称
            if (StringUtils.isNotEmpty(queryRequest.getBrandName())) {
                predicates.add(cbuild.like(root.get("brandName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getBrandName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 缺货数量
            if (queryRequest.getStockoutNum() != null) {
                predicates.add(cbuild.equal(root.get("stockoutNum"), queryRequest.getStockoutNum()));
            }

            // 模糊查询 - 缺货地区
            if (StringUtils.isNotEmpty(queryRequest.getStockoutCity())) {
                predicates.add(cbuild.like(root.get("stockoutCity"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getStockoutCity()))
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

            // 大于或等于 搜索条件:缺货时间开始
            if (queryRequest.getStockoutBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("stockoutTime"),
                        queryRequest.getStockoutBegin()));
            }
            // 小于或等于 搜索条件:缺货改时间截止
            if (queryRequest.getStockoutEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("stockoutTime"),
                        queryRequest.getStockoutEnd()));
            }

            // 大于或等于 搜索条件:补货时间开始
            if (queryRequest.getReplenishmentTimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("replenishmentTime"),
                        queryRequest.getReplenishmentTimeBegin()));
            }
            // 小于或等于 搜索条件:补货改时间截止
            if (queryRequest.getReplenishmentTimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("replenishmentTime"),
                        queryRequest.getReplenishmentTimeEnd()));
            }

            // 删除标识,0:未删除1:已删除
            if (queryRequest.getDelFlag() != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }

            if (queryRequest.getAddedFlag() != null) {
                predicates.add(cbuild.equal(root.get("addedFlag"), queryRequest.getAddedFlag()));
            }

            // 补货标识,0:暂未补齐1:已经补齐:2缺货提醒
            if (queryRequest.getReplenishmentFlag() != null) {
                predicates.add(cbuild.equal(root.get("replenishmentFlag"), queryRequest.getReplenishmentFlag()));
            }

            if (queryRequest.getReplenishmentFlagList() != null) {
                predicates.add(root.get("replenishmentFlag").in(queryRequest.getReplenishmentFlagList()));
            }

            // 店铺id
            if (queryRequest.getStoreId() != null) {
                predicates.add(cbuild.equal(root.get("storeId"), queryRequest.getStoreId()));
            }

            if (queryRequest.getWareId() != null) {
                predicates.add(cbuild.equal(root.get("wareId"), queryRequest.getWareId()));
            }

            if (queryRequest.getStockoutDay() != null) {
                predicates.add(cbuild.equal(root.get("stockoutDay"), queryRequest.getStockoutDay()));
            }

            if (queryRequest.getSource() != null) {
                predicates.add(cbuild.equal(root.get("source"), queryRequest.getSource()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
