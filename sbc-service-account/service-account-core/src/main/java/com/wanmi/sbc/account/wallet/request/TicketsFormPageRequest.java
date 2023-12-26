package com.wanmi.sbc.account.wallet.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.account.wallet.model.root.CustomerWallet;
import com.wanmi.sbc.account.wallet.model.root.TicketsForm;
import com.wanmi.sbc.account.wallet.model.root.TicketsFormQuery;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.XssUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.Column;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 *工单请求参数
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketsFormPageRequest extends BaseQueryRequest {

    /***
     * 工单类型
     */
    @ApiModelProperty(value = "工单类型 1充值，2提现,3调账")
    private Integer applyType;

    /**
     * 提现状态
     */
    @ApiModelProperty(value = "提现申请单状态【1待审核，2已审核，3已打款，4已拒绝】")
    private Integer extractStatus;

    @ApiModelProperty(value = "客户账户")
    private String customerAccount;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime beginTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "申请时间")
    private LocalDateTime applyTime;

    @ApiModelProperty(value = "钱包ID")
    private String walletId;

    @ApiModelProperty(value = "客户电话")
    private String customerPhone;

    @ApiModelProperty(value = "客服审核时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime auditTimeStart;


    @ApiModelProperty(value = "客服审核时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime auditTimeEnd;

    @ApiModelProperty(value = "提现金额")
    private BigDecimal applyPrice;


    @ApiModelProperty(value = "提现起始金额")
    private BigDecimal startApplyPrice;

    @ApiModelProperty(value = "提现结束金额")
    private BigDecimal endApplyPrice;

    @ApiModelProperty(value = "财务审核开始时间")
    private LocalDateTime financialTimeStart;

    @ApiModelProperty(value = "财务审核结束时间")
    private LocalDateTime financialTimeEnd;

    @ApiModelProperty(value = "交易单编号列表")
    private List<String> stockoutIdList;

    @ApiModelProperty(value = "formId")
    private List<Long> formIds;


    /**
     * 自动申请0否 1是
     */
    @ApiModelProperty(value = "autoType")
    private Integer autoType;

    public Specification<TicketsFormQuery> getWhereCriteria(){
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<TicketsFormQuery, CustomerWallet> customerWalletJoin = root.join("customerWallet");
            if(Objects.nonNull(applyType)){
                predicates.add(cbuild.equal(root.get("applyType"),applyType));
            }

            // 处理提现金额
            if(Objects.nonNull(startApplyPrice) && Objects.nonNull(endApplyPrice)){//添加提现金额过滤
                predicates.add(cbuild.between(root.get("applyPrice"),startApplyPrice,endApplyPrice));
            }else if(Objects.nonNull(startApplyPrice)){
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("applyPrice"),startApplyPrice));
            }else if(Objects.nonNull(endApplyPrice)){
                predicates.add(cbuild.lessThanOrEqualTo(root.get("applyPrice"),endApplyPrice));
            }


            if(!CollectionUtils.isEmpty(stockoutIdList)){
                predicates.add(root.get("recordNo").in(stockoutIdList));
            }

            if(!CollectionUtils.isEmpty(formIds)){
                predicates.add(root.get("formId").in(formIds));
            }

            if(Objects.nonNull(extractStatus)){
                predicates.add(cbuild.equal(root.get("extractStatus"),extractStatus));
            }
            if(StringUtils.isNotEmpty(walletId)){
                predicates.add(cbuild.equal(root.get("walletId"),walletId));
            }
            if(StringUtils.isNotEmpty(customerAccount)){
                predicates.add(cbuild.like(customerWalletJoin.get("customerAccount"), new StringBuffer().append("%").append(
                        XssUtils.replaceLikeWildcard(customerAccount.trim())).append("%").toString()));
            }
            if(StringUtils.isNotEmpty(customerName)){
                predicates.add(cbuild.like(root.get("customerName"), new StringBuffer().append("%").append(
                        XssUtils.replaceLikeWildcard(customerName.trim())).append("%").toString()));
            }
            if(StringUtils.isNotEmpty(customerPhone)){
                predicates.add(cbuild.like(root.get("customerPhone"), new StringBuffer().append("%").append(
                        XssUtils.replaceLikeWildcard(customerPhone.trim())).append("%").toString()));
            }

            if(Objects.nonNull(beginTime) && Objects.nonNull(endTime)){
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("applyTime"), beginTime));// >=
                predicates.add(cbuild.lessThanOrEqualTo(root.get("applyTime"), endTime));// <=
            }
            if (!CollectionUtils.isEmpty(formIds)) {
                CriteriaBuilder.In<Long> in = cbuild.in(root.<Long>get("formId"));
                for (Long formId : formIds) {
                    in.value(formId);
                }
                predicates.add(in);
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

    public static Date getFirstDay(int year, int month) {
        // 获取Calendar类的实例
        Calendar c = Calendar.getInstance();
        // 设置年份
        c.set(Calendar.YEAR, year);
        // 设置月份，因为月份从0开始，所以用month - 1
        c.set(Calendar.MONTH, month - 1);
        // 设置日期
        c.set(Calendar.DAY_OF_MONTH, 1);

        return c.getTime();
    }

    public static Date getLastDay(int year, int month) {
        // 获取Calendar类的实例
        Calendar c = Calendar.getInstance();
        // 设置年份
        c.set(Calendar.YEAR, year);
        // 设置月份，因为月份从0开始，所以用month - 1
        c.set(Calendar.MONTH, month - 1);
        // 获取当前时间下，该月的最大日期的数字
        int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        // 将获取的最大日期数设置为Calendar实例的日期数
        c.set(Calendar.DAY_OF_MONTH, lastDay);

        return c.getTime();
    }
}