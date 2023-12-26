package com.wanmi.sbc.customer.api.response.invitationstatistics;

import com.google.common.collect.Lists;
import com.wanmi.sbc.customer.bean.vo.InvitationStatisticsVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>邀新统计列表结果</p>
 * @author lvheng
 * @date 2021-04-23 10:57:45
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationStatisticsListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 邀新统计列表结果
     */
    @ApiModelProperty(value = "邀新统计列表结果")
    private List<InvitationStatisticsVO> invitationStatisticsVOList = Lists.newArrayList();
}
