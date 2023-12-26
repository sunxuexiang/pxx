package com.wanmi.sbc.customer.api.response.invitationhistorysummary;

import com.wanmi.sbc.customer.bean.vo.InvitationHistorySummaryVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>邀新历史汇总计表修改结果</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationHistorySummaryModifyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的邀新历史汇总计表信息
     */
    @ApiModelProperty(value = "已修改的邀新历史汇总计表信息")
    private InvitationHistorySummaryVO invitationHistorySummaryVO;
}
