package com.wanmi.sbc.goods.goodsunit.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.goods.goodsunit.root.StoreGoodsUnit;
import com.wanmi.sbc.goods.util.XssUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品单位
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsUnitQueryRequest extends BaseQueryRequest {


    /**
     * and 精准查询，厂商名称
     */
    private String unit;
    /**
     * 判断
     */

    private String unitCount;

    private String status;
    /**
     * 删除标记
     */
    private Integer delFlag;

    private Long companyInfoId;

    private List<Long> companyInfoIds;

    /**
     * 封装公共条件
     * @return
     */
    public Specification<StoreGoodsUnit> getWhereCriteria(){
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(unitCount!=null){
                predicates.add(cbuild.equal(root.get("unit"),unitCount));
            }
            //模糊查询昵称
            if(StringUtils.isNotEmpty(unit)){
                predicates.add(cbuild.like(root.get("unit"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(unit).trim()).concat(StringUtil.SQL_LIKE_CHAR)));
            }

            if (CollectionUtils.isNotEmpty(companyInfoIds)) {
                CriteriaBuilder.In in = cbuild.in(root.get("companyInfoId"));
                for (Long storeId : companyInfoIds) {
                    in.value(storeId);
                }
                predicates.add(in);
            }

            if(delFlag!=null){
                predicates.add(cbuild.equal(root.get("delFlag"),delFlag));
            }
            if(status!=null){
                predicates.add(cbuild.equal(root.get("status"),status));
            }
            if (companyInfoId!=null) {
                predicates.add(cbuild.equal(root.get("companyInfoId"), companyInfoId));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
    /**
     * 封装公共条件
     * @return
     */
    public Specification<StoreGoodsUnit> getCount(){
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //模糊查询昵称
            if(unit!=null){
                predicates.add(cbuild.equal(root.get("unit"),unit));
            }
            if(delFlag!=null){
                predicates.add(cbuild.equal(root.get("delFlag"),delFlag));
            }
            if(status!=null){
                predicates.add(cbuild.equal(root.get("status"),status));
            }
            if (companyInfoId!=null) {
                predicates.add(cbuild.equal(root.get("companyInfoId"), companyInfoId));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
