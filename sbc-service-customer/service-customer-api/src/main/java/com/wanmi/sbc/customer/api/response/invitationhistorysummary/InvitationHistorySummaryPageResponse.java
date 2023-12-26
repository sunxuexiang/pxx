package com.wanmi.sbc.customer.api.response.invitationhistorysummary;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.customer.bean.vo.InvitationHistorySummaryVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>邀新历史汇总计表分页结果</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationHistorySummaryPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 邀新历史汇总计表分页结果
     */
    @ApiModelProperty(value = "邀新历史汇总计表分页结果")
    private MicroServicePage<InvitationHistorySummaryVO> invitationHistorySummaryVOPage;
}
