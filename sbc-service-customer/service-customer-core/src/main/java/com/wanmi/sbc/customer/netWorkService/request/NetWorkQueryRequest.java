package com.wanmi.sbc.customer.netWorkService.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.customer.netWorkService.model.root.NetWork;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 查询网点多条件类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetWorkQueryRequest extends BaseQueryRequest {

    /**
     * 主键id
     */

    private Long networkId;

    private List<Long> networkIds;

    /**
     * 网点联系人
     */

    private String contacts;

    /**
     * 网点名字
     */

    private String networkName;

    /**
     * 网点手机号码
     */

    private String phone;


    /**
     * 网点座机号码
     */

    private String landline;


    /**
     * 网点地址
     */

    private String networkAddress;

    /**
     * 省
     */

    private String province;


    /**
     * 市
     */

    private String city;

    /**
     * 纬度值
     */

    private BigDecimal lat;

    /**
     * 经度值
     */

    private BigDecimal lng;

    /**
     * 可配送距离 米
     */

    private int distance;

    /**
     * 是否删除
     */

    private Integer delFlag;


    /**
     * 封装公共条件
     *
     * @return
     */
    public Specification<NetWork> getWhereCriteria() {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //网点id
            if (Objects.nonNull(networkId)) {
                predicates.add(cbuild.equal(root.get("networkId"), networkId));
            }
            //批量网点id
            if(CollectionUtils.isNotEmpty(networkIds)){
                predicates.add(root.get("networkId").in(networkIds));
            }
            //网点联系人
            if(StringUtils.isNotBlank(contacts)){
                predicates.add(cbuild.equal(root.get("contacts"), contacts));
            }
            //网点名字
//            if(StringUtils.isNotBlank(networkName)){
//                predicates.add(cbuild.equal(root.get("networkName"), networkName));
//            }

            //模糊查询名称
            if(StringUtils.isNotEmpty(networkName)){
                predicates.add(cbuild.like(root.get("networkName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(networkName.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }

            //网点手机号码
            if(StringUtils.isNotBlank(phone)){
                predicates.add(cbuild.equal(root.get("phone"), phone));
            }
            //网点座机号码
            if(StringUtils.isNotBlank(landline)){
                predicates.add(cbuild.equal(root.get("landline"), landline));
            }

            //网点地址
            if(StringUtils.isNotBlank(networkAddress)){
                predicates.add(cbuild.equal(root.get("networkAddress"), networkAddress));
            }
            //是否删除
            if(Objects.nonNull(delFlag)){
                predicates.add(cbuild.equal(root.get("delFlag"), delFlag));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

}
