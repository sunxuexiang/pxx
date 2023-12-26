package com.wanmi.sbc.goods.storecate.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.goods.storecate.model.root.StoreCate;
import com.wanmi.sbc.goods.storecate.model.root.StoreCateGoodsRela;
import com.wanmi.sbc.goods.util.XssUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: bail
 * Time: 2017/11/13.10:22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreCateGoodsRelaQueryRequest extends BaseQueryRequest {

    /**
     * 店铺分类标识
     */
    private Long storeCateId;

    /**
     * 批量店铺分类标识
     */
    private List<String> goodsIds;

    /**
     * 店铺标识
     */
    private Long goodsId;



    /**
     * 封装公共的查询条件
     * @return
     */
    public Specification<StoreCateGoodsRela> getWhereCriteria(){
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //批量分类id
            if(CollectionUtils.isNotEmpty(goodsIds)){
                predicates.add(root.get("goodsId").in(goodsIds));
            }
            if(predicates.isEmpty()){
                return null;
            }else if(predicates.size() == 1){
                return predicates.get(0);
            }else{
                return cbuild.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
