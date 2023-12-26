package com.wanmi.sbc.live.api.request.stream;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取设置加购、立购、领取优惠卷人数
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveStreamSetRequest {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "0 加购 1 立购 2领取优惠卷 3点赞 4在线人数")
    private Integer type;

    @ApiModelProperty(value = "直播id")
    private Integer liveId;

    /**
     * 账号id
     */
    @ApiModelProperty(value = "账号id")
    private String customerId;
}
