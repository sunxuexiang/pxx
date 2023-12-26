package com.wanmi.sbc.account.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketsFormLogVo implements Serializable {

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

    @ApiModelProperty(value = "审核人员姓名")
    private String auditStaffName;

    /**
     * 审核时间
     */
    @ApiModelProperty(value = "审核时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime auditTime;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

}
