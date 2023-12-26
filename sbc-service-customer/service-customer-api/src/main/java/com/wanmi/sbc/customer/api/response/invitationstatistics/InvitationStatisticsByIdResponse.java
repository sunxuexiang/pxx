package com.wanmi.sbc.customer.api.response.invitationstatistics;

import com.wanmi.sbc.customer.bean.vo.InvitationStatisticsVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>根据id查询任意（包含已删除）邀新统计信息response</p>
 * @author lvheng
 * @date 2021-04-23 10:57:45
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationStatisticsByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 邀新统计信息
     */
    @ApiModelProperty(value = "邀新统计信息")
    private InvitationStatisticsVO invitationStatisticsVO;
}
