package com.wanmi.sbc.wallet.wallet.request;


import com.wanmi.sbc.wallet.wallet.model.root.VirtualGoods;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jeffrey
 * @create 2021-08-27 14:28
 */
@Data
public class VirtualGoodsQueryRequest extends BaseQueryRequest {

    @ApiModelProperty(value = "删除标识")
    private Integer delFlag;

    public static Specification<VirtualGoods> build(VirtualGoodsQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 客户帐号
            if (queryRequest.getDelFlag() !=null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
