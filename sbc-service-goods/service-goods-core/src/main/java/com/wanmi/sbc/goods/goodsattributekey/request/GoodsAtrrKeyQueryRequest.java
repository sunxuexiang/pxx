package com.wanmi.sbc.goods.goodsattributekey.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.goods.goodsattributekey.root.GoodsAttributeKey;
import com.wanmi.sbc.goods.util.XssUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.Column;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品属性
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsAtrrKeyQueryRequest extends BaseQueryRequest {

    private List<String> attributeIds;

    /**
     * 属性id
     */

    private String goodsAttributeId;
    /**
     * 属性 名称
     */

    private String goodsAttributeValue;
    /**
     * 商品明细id
     */

    private String goodsInfoId;
    /**
     * 商品id
     */
    private String goodsId;
    /**
     * 封装公共条件
     * @return
     */
    public Specification<GoodsAttributeKey> getWhereCriteria(){
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //模糊查询昵称
            if(StringUtils.isNotEmpty(goodsAttributeValue)){
                predicates.add(cbuild.like(root.get("goodsAttributeValue"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(goodsAttributeValue).trim()).concat(StringUtil.SQL_LIKE_CHAR)));
            }
            if(StringUtils.isNotEmpty(goodsId)){
                predicates.add(cbuild.equal(root.get("goodsId"), goodsId));
            }
            if(StringUtils.isNotEmpty(goodsInfoId)){
                predicates.add(cbuild.equal(root.get("goodsInfoId"), goodsInfoId));
            }
            if(StringUtils.isNotEmpty(goodsAttributeId)){
                predicates.add(cbuild.equal(root.get("goodsAttributeId"), goodsAttributeId));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
