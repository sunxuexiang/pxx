package com.wanmi.sbc.wallet.api.request.wallet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.XssUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
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

    @ApiModelProperty(value = "钱包ID")
    private String walletId;

    /***
     * 工单类型
     */
    @ApiModelProperty(value = "工单类型 1充值，2提现")
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

    @ApiModelProperty(value = "客户电话")
    private String customerPhone;

    @ApiModelProperty(value = "申请时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime beginTime;

    @ApiModelProperty(value = "申请时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;


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


    @ApiModelProperty(value = "交易单编号列表")
    private List<String> stockoutIdList;

    @ApiModelProperty(value = "id列表")
    private List<Long> formIds;

    @ApiModelProperty(value = "财务审核开始时间")
    private LocalDateTime financialTimeStart;

    @ApiModelProperty(value = "财务审核结束时间")
    private LocalDateTime financialTimeEnd;

}