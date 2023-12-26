package com.wanmi.sbc.goods.biddinggoods.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsQueryRequest;
import com.wanmi.sbc.goods.biddinggoods.model.root.BiddingGoods;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.apache.commons.lang3.StringUtils;
import com.wanmi.sbc.common.util.XssUtils;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>竞价商品动态查询条件构建器</p>
 * @author baijz
 * @date 2020-08-05 16:34:44
 */
public class BiddingGoodsWhereCriteriaBuilder {
    public static Specification<BiddingGoods> build(BiddingGoodsQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {

            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-竞价商品的IdList
            if (CollectionUtils.isNotEmpty(queryRequest.getBiddingGoodsIdList())) {
                predicates.add(root.get("biddingGoodsId").in(queryRequest.getBiddingGoodsIdList()));
            }
            // 批量查询- 竞价的Ids
            if(CollectionUtils.isNotEmpty(queryRequest.getBiddingIds())){
                predicates.add(root.get("biddingId").in(queryRequest.getBiddingIds()));
            }

            // 竞价商品的Id
            if (StringUtils.isNotEmpty(queryRequest.getBiddingGoodsId())) {
                predicates.add(cbuild.equal(root.get("biddingGoodsId"), queryRequest.getBiddingGoodsId()));
            }

            // 模糊查询 - 竞价的Id
            if (StringUtils.isNotEmpty(queryRequest.getBiddingId())) {
                predicates.add(cbuild.like(root.get("biddingId"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getBiddingId()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 排名
            if (queryRequest.getSort() != null) {
                predicates.add(cbuild.equal(root.get("sort"), queryRequest.getSort()));
            }

            // 模糊查询 - skuId
            if (StringUtils.isNotEmpty(queryRequest.getGoodsInfoId())) {
                predicates.add(cbuild.like(root.get("goodsInfoId"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getGoodsInfoId()))
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

            // 大于或等于 搜索条件:删除时间开始
            if (queryRequest.getDelTimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("delTime"),
                        queryRequest.getDelTimeBegin()));
            }
            // 小于或等于 搜索条件:删除时间截止
            if (queryRequest.getDelTimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("delTime"),
                        queryRequest.getDelTimeEnd()));
            }

            // 删除标志
            if (queryRequest.getDelFlag() != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
