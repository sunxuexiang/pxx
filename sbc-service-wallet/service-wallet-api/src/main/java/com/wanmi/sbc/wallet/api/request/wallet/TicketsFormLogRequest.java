package com.wanmi.sbc.wallet.api.request.wallet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.wallet.api.request.BalanceBaseRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketsFormLogRequest extends BalanceBaseRequest {

    /**
     * 提现申请日志id
     */
    @ApiModelProperty(value = "提现申请日志id")
    private Long ticketsFormLogId;

    /**
     * 业务id/表主键
     */
    @ApiModelProperty(value = "业务id/表主键")
    private Long businessId;

    /**
     * 审核人员类型:1客户 2财务
     */
    @ApiModelProperty(value = "审核人员类型: 1客户 2财务")
    private Integer auditStaffType;

    /**
     * 审核状态:1通过 2不通过 3打款失败
     */
    @ApiModelProperty(value = "审核状态: 1通过 2不通过 3打款失败")
    private Integer auditStatus;

    /**
     * 审核人员
     */
    @ApiModelProperty(value = "审核人员")
    private String auditStaff;

    /**
     * 审核时间
     */
    @ApiModelProperty(value = "审核时间")
    private LocalDateTime auditTime;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "客服审核时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime auditTimeStart;


    @ApiModelProperty(value = "客服审核时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime auditTimeEnd;

    /**
     * 业务id/表主键
     */
    @ApiModelProperty(value = "业务id/表主键")
    private List<Long> businessIds;

}
