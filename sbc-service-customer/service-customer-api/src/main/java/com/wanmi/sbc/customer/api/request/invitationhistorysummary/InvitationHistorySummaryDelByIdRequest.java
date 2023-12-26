package com.wanmi.sbc.customer.api.request.invitationhistorysummary;

import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>单个删除邀新历史汇总计表请求参数</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationHistorySummaryDelByIdRequest extends CustomerBaseRequest {
private static final long serialVersionUID = 1L;

    /**
     * 业务员ID
     */
    @ApiModelProperty(value = "业务员ID")
    @NotNull
    private String employeeId;
}
