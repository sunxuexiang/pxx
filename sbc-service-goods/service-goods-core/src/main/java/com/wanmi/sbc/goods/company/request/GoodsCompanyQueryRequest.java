package com.wanmi.sbc.goods.company.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.company.model.root.GoodsCompany;
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
 * 品牌查询请求
 * Created by daiyitian on 2017/3/24.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsCompanyQueryRequest extends BaseQueryRequest {


    /**
     * and 精准查询，厂商名称
     */
    private String companyName;

    private String status;
    /**
     * 删除标记
     */
    private Integer delFlag;
    /**
     * 封装公共条件
     * @return
     */
    public Specification<GoodsCompany> getWhereCriteria(){
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //模糊查询昵称
            if(StringUtils.isNotEmpty(companyName)){
                predicates.add(cbuild.like(root.get("companyName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(companyName).trim()).concat(StringUtil.SQL_LIKE_CHAR)));
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
