package com.wanmi.sbc.returnorder.pilepurchaseaction;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
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
 * @description: 囤货明细请求类
 * @author: XinJiang
 * @time: 2021/12/20 15:17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PilePurchaseActionRequest extends BaseQueryRequest {

    private static final long serialVersionUID = -6268445614872307929L;

    /**
     * 会员编号
     */
    private String customerId;

    /**
     * SKU编号
     */
    private String goodsInfoId;
    /**
     * 批量SKU编号
     */
    private List<String> goodsInfoIds;
    /**
     * 批量sku
     */
    private List<GoodsInfoDTO> goodsInfos;

    /**
     * 批量囤货明细信息
     */
    private List<PilePurchaseAction> purchaseActionList;

    /**
     * 是否后台同步标识
     */
    private Boolean syncFlag;

    /**
     * 封装公共条件
     *
     * @return
     */
    public Specification<PilePurchaseAction> getWhereCriteria() {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();

            //客户编号
            if (StringUtils.isNotBlank(customerId)) {
                predicates.add(cbuild.equal(root.get("customerId"), customerId));
            }
            //SKU编号
            if (StringUtils.isNotBlank(goodsInfoId)) {
                predicates.add(cbuild.equal(root.get("goodsInfoId"), goodsInfoId));
            }
            //批量SKU编号
            if (CollectionUtils.isNotEmpty(goodsInfoIds)) {
                predicates.add(root.get("goodsInfoId").in(goodsInfoIds));
            }

            if (syncFlag) {
                predicates.add(cbuild.or(cbuild.isNull(root.get("goodsSplitPrice")),cbuild.isNull(root.get("pid"))));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
