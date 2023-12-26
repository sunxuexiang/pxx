package com.wanmi.sbc.goods.goodsattribute.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.GoodsSelectStatus;
import com.wanmi.sbc.goods.goodsattribute.root.GoodsAttribute;
import com.wanmi.sbc.goods.info.model.root.BulkGoods;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.model.root.RetailGoods;
import com.wanmi.sbc.goods.util.XssUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
public class GoodsAtrrQueryRequest extends BaseQueryRequest {

    private List<String> attributeIds;

    /**
     * and 精准查询，厂商名称
     */
    private String attribute;

    private String status;
    /**
     * 删除标记
     */
    private Integer delFlag;
    /**
     * 封装公共条件
     * @return
     */
    public Specification<GoodsAttribute> getWhereCriteria(){
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //模糊查询昵称
            if(StringUtils.isNotEmpty(attribute)){
                predicates.add(cbuild.like(root.get("attribute"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(attribute).trim()).concat(StringUtil.SQL_LIKE_CHAR)));
            }
            if(delFlag!=null){
                predicates.add(cbuild.equal(root.get("delFlag"),delFlag));
            }
            if(status!=null){
                predicates.add(cbuild.equal(root.get("status"),status));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
