package com.wanmi.sbc.goods.goodsBackups.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.goods.goodsBackups.root.GoodsRecommendBackups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品推荐备份
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsBackupsQueryRequest extends BaseQueryRequest {
    /**
     * 用户
     */
    private String customerId;
    /**
     * 公司
     */
    private Long companyId;
    /**
     * 封装公共条件
     * @return
     */
    public Specification<GoodsRecommendBackups> getWhereCriteria(){
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(customerId!=null){
                predicates.add(cbuild.equal(root.get("customerId"),customerId));
            }

            if (companyId!=null) {
                predicates.add(cbuild.equal(root.get("companyId"), companyId));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
    /**
     * 封装公共条件
     * @return
     */
    public Specification<GoodsRecommendBackups> getCount(){
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(customerId!=null){
                predicates.add(cbuild.equal(root.get("customerId"),customerId));
            }

            if (companyId!=null) {
                predicates.add(cbuild.equal(root.get("companyInfoId"), companyId));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
