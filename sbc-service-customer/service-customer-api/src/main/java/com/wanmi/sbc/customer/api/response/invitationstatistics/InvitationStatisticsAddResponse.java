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
 * <p>邀新统计新增结果</p>
 * @author lvheng
 * @date 2021-04-23 10:57:45
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationStatisticsAddResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已新增的邀新统计信息
     */
    @ApiModelProperty(value = "已新增的邀新统计信息")
    private InvitationStatisticsVO invitationStatisticsVO;
}
