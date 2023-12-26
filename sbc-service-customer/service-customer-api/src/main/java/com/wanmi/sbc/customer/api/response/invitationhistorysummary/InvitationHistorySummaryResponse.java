package com.wanmi.sbc.customer.api.response.invitationhistorysummary;

import com.wanmi.sbc.customer.bean.vo.InvitationHistorySummaryVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>根据id查询任意（包含已删除）邀新历史汇总计表信息response</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationHistorySummaryResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "当日邀新数")
    private InvitationHistorySummaryVO todayInvitationSummaryVO ;


    @ApiModelProperty(value = "当月邀新数")
    private InvitationHistorySummaryVO monthInvitationSummaryVO ;


    @ApiModelProperty(value = "总计邀新数")
    private InvitationHistorySummaryVO totalInvitationSummaryVO ;




}
