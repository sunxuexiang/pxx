package com.wanmi.sbc.customer.api.response.invitationhistorysummary;

import com.wanmi.sbc.customer.bean.vo.InvitationHistorySummaryVO;
import com.wanmi.sbc.customer.bean.vo.InvitationStatisticsVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

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
public class InvitationHistorySummaryByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 邀新历史汇总计表信息
     */
    @ApiModelProperty(value = "总计邀新数")
    private InvitationHistorySummaryVO totalInvitationSummaryVO;


}
