package com.wanmi.sbc.goods.stockoutdetail.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.goods.api.request.stockoutdetail.StockoutDetailQueryRequest;
import com.wanmi.sbc.goods.stockoutdetail.model.root.StockoutDetail;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.apache.commons.lang3.StringUtils;
import com.wanmi.sbc.common.util.XssUtils;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>缺货管理动态查询条件构建器</p>
 * @author tzx
 * @date 2020-05-27 11:37:12
 */
public class StockoutDetailWhereCriteriaBuilder {
    public static Specification<StockoutDetail> build(StockoutDetailQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-缺货明细List
            if (CollectionUtils.isNotEmpty(queryRequest.getStockoutDetailIdList())) {
                predicates.add(root.get("stockoutDetailId").in(queryRequest.getStockoutDetailIdList()));
            }

            // 批量查询-缺货明细List
            if (CollectionUtils.isNotEmpty(queryRequest.getGoodsInfoIdLsit())) {
                predicates.add(root.get("goodsInfoId").in(queryRequest.getGoodsInfoIdLsit()));
            }

            // 缺货明细
            if (StringUtils.isNotEmpty(queryRequest.getStockoutDetailId())) {
                predicates.add(cbuild.equal(root.get("stockoutDetailId"), queryRequest.getStockoutDetailId()));
            }

            // 模糊查询 - 缺货列表id
            if (StringUtils.isNotEmpty(queryRequest.getStockoutId())) {
                predicates.add(cbuild.like(root.get("stockoutId"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getStockoutId()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 会员id
            if (StringUtils.isNotEmpty(queryRequest.getCustomerId())) {
                predicates.add(cbuild.like(root.get("customerId"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getCustomerId()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - sku id
            if (StringUtils.isNotEmpty(queryRequest.getGoodsInfoId())) {
                predicates.add(cbuild.like(root.get("goodsInfoId"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getGoodsInfoId()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - sku编码
            if (StringUtils.isNotEmpty(queryRequest.getGoodsInfoNo())) {
                predicates.add(cbuild.like(root.get("goodsInfoNo"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getGoodsInfoNo()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 缺货数量
            if (queryRequest.getStockoutNum() != null) {
                predicates.add(cbuild.equal(root.get("stockoutNum"), queryRequest.getStockoutNum()));
            }

            // 模糊查询 - 缺货市code
            if (StringUtils.isNotEmpty(queryRequest.getCityCode())) {
                predicates.add(cbuild.like(root.get("cityCode"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getCityCode()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 下单人详细地址
            if (StringUtils.isNotEmpty(queryRequest.getAddress())) {
                predicates.add(cbuild.like(root.get("address"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getAddress()))
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

            // 删除标识,0:未删除1:已删除
            if (queryRequest.getDelFlag() != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
