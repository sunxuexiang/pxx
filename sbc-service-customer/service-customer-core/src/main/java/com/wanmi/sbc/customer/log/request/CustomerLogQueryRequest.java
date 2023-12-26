package com.wanmi.sbc.customer.log.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.customer.log.model.root.CustomerLog;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @description  
 * @author  shiy
 * @date    2023/4/8 9:28
 * @params  
 * @return  
*/
@Data
public class CustomerLogQueryRequest extends BaseQueryRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    private List<String> userNoList;

    private String userNo;

    /**
     * app类型
     */
    private Integer appType;


    /**
     * app版本
     */
    private String appVersion;

    /**
     * 设备信息
     */
    private String devInfo;

    /**
     * 1APP登录
     */
    private Integer logType;


    /**
     * 搜索条件:创建时间开始
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTimeBegin;
    /**
     * 搜索条件:创建时间截止
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTimeEnd;

    /**
     * 封装公共条件
     *
     * @return
     */
    public Specification<CustomerLog> getWhereCriteria() {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (null!=createTimeBegin) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("createTime"), createTimeBegin));
            }
            if (null!=createTimeEnd) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("createTime"), createTimeEnd));
            }
            if(StringUtils.isNotBlank(appVersion)){
                predicates.add(cbuild.equal(root.get("appVersion"), appVersion));
            }
            if(StringUtils.isNotBlank(userNo)){
                predicates.add(cbuild.equal(root.get("userNo"), userNo));
            }
            if(null!=appType){
                predicates.add(cbuild.equal(root.get("appType"), appType));
            }
            if(null!=logType) {
                predicates.add(cbuild.equal(root.get("logType"), logType));
            }
            if(StringUtils.isNotBlank(devInfo)){
                predicates.add(cbuild.like(root.get("devInfo"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(devInfo))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }
            if (!CollectionUtils.isEmpty(userNoList)) {
                CriteriaBuilder.In in = cbuild.in(root.get("userNo"));
                for (String userNo : userNoList) {
                    in.value(userNo);
                }
                predicates.add(in);
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

}