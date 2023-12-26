package com.wanmi.sbc.live.api.request.bag;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveBagListRequest implements Serializable {
    private static final long serialVersionUID = 9162837125836399748L;
    /**
     * 直播间记录id
     */
    @ApiModelProperty(value = "直播间记录id")
    private Integer liveId;

    @ApiModelProperty(value = "直播间id")
    private Integer liveRoomId;

    @ApiModelProperty(value = "福袋名称")
    private String bagName;

    @ApiModelProperty(value = "发放状态：0：未发放，1：已发放")
    private Long provideStatus;
}
