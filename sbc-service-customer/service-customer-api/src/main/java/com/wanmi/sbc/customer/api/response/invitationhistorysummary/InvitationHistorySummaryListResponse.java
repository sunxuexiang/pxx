package com.wanmi.sbc.customer.api.response.invitationhistorysummary;

import com.wanmi.sbc.customer.bean.vo.InvitationHistorySummaryVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>邀新历史汇总计表列表结果</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationHistorySummaryListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 邀新历史汇总计表列表结果
     */
    @ApiModelProperty(value = "邀新历史汇总计表列表结果")
    private List<InvitationHistorySummaryVO> invitationHistorySummaryVOList;
}
