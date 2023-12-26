package com.wanmi.sbc.setting.doorpick.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.setting.doorpick.model.root.DoorPickConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 查询上门自提配置多条件类
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoorPickConfigQueryRequest extends BaseQueryRequest {

    /**
     * 主键id
     */

    private Long networkId;

    private List<Long> networkIds;

    /**
     * 上门自提配置联系人
     */

    private String contacts;

    /**
     * 上门自提配置名字
     */

    private String networkName;

    /**
     * 上门自提配置手机号码
     */

    private String phone;


    /**
     * 上门自提配置座机号码
     */

    private String landline;


    /**
     * 上门自提配置地址
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
     * 0 启用，1，删除，2停用
     */

    private Integer delFlag;

    /**
     * 是否删除
     */

    private Long storeId;


    /**
     * 封装公共条件
     *
     * @return
     */
    public Specification<DoorPickConfig> getWhereCriteria() {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //上门自提配置id
            if (Objects.nonNull(networkId)) {
                predicates.add(cbuild.equal(root.get("networkId"), networkId));
            }
            //批量上门自提配置id
            if(CollectionUtils.isNotEmpty(networkIds)){
                predicates.add(root.get("networkId").in(networkIds));
            }
            //上门自提配置联系人
            if(StringUtils.isNotBlank(contacts)){
                predicates.add(cbuild.equal(root.get("contacts"), contacts));
            }
            //上门自提配置名字
//            if(StringUtils.isNotBlank(networkName)){
//                predicates.add(cbuild.equal(root.get("networkName"), networkName));
//            }

            //模糊查询名称
            if(StringUtils.isNotEmpty(networkName)){
                predicates.add(cbuild.like(root.get("networkName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(networkName.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }

            //上门自提配置手机号码
            if(StringUtils.isNotBlank(phone)){
                predicates.add(cbuild.equal(root.get("phone"), phone));
            }
            //上门自提配置座机号码
            if(StringUtils.isNotBlank(landline)){
                predicates.add(cbuild.equal(root.get("landline"), landline));
            }
            //上门自提配置地址
            if(StringUtils.isNotBlank(networkAddress)){
                predicates.add(cbuild.equal(root.get("networkAddress"), networkAddress));
            }
            //是否删除
            if(Objects.nonNull(delFlag)){
                predicates.add(cbuild.equal(root.get("delFlag"), delFlag));
            }
            //门店Id
            if(Objects.nonNull(storeId)){
                predicates.add(cbuild.equal(root.get("storeId"), storeId));
            }
            predicates.add(root.get("delFlag").in(Arrays.asList(0,2)));
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

}
