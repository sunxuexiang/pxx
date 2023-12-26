package com.wanmi.sbc.account.api.request.wallet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.account.api.request.AccountBaseRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketsFormAdoptRequest extends AccountBaseRequest {
    private static final long serialVersionUID = 2321329611892586232L;
    @ApiModelProperty(value = "工单id")
    private List<Long> formIds;

    @ApiModelProperty(value = "审核备注")
    private String remark;

    @ApiModelProperty(value = "打款失败/驳回（4：驳回；5：失败）")
    private Integer extractStatus;

    /**
     * 审核人传入
     */
    @ApiModelProperty(value = "审核人")
    private String auditAdmin;

    //===========客户审核传入=============//
    @ApiModelProperty(value = "银行卡卡号")
    private String bankCode;

    @ApiModelProperty(value = "账户名称/支行")
    private String bankBranch;

    @ApiModelProperty(value = "开户行")
    private String bankName;

    @ApiModelProperty(value = "备注")
    private String backRemark;

    //===========财务审核传入=============//

    @ApiModelProperty(value = "转账账户id")
    private Long accountId;

    @ApiModelProperty(value = "转账日期")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime transferDate;

    @ApiModelProperty(value = "转账银行卡号")
    private String bankNo;

    @ApiModelProperty(value = "打款凭证图片地址")
    private List<String> ticketsFormPaymentVoucherImgList;

    //禁止修改到账金额, 此字段废除
    @ApiModelProperty(value = "到账金额")
    private BigDecimal arrivalPrice;
}
