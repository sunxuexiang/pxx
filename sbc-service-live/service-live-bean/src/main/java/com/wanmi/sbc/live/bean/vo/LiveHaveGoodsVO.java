package com.wanmi.sbc.live.bean.vo;

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
public class LiveHaveGoodsVO implements Serializable {
    private static final long serialVersionUID = 8666702470267150944L;

    @ApiModelProperty(value = "'是否有直播间")
    private Integer isHaveLive;
    /**
     * 讲解标识,0:未讲解1:讲解中
     */
    @ApiModelProperty(value = "讲解标识,0:未讲解1:讲解中")
    private Integer explainFlag;

    @ApiModelProperty(value = "'直播间id")
    private Integer liveRoomId;

    /**
     * 直播id
     */
    @ApiModelProperty(value = "直播liveId")
    private Integer liveId;

    @ApiModelProperty(value = "官方直播间")
    private Integer sysFlag;
}
