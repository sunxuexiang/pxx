package com.wanmi.sbc.live.api.response.activity;

import com.wanmi.sbc.live.bean.vo.LiveStreamActivityVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveStreamActivityListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 直播活动列表
     */
    @ApiModelProperty(value = "直播活动list")
    private List<LiveStreamActivityVO> activityVOList;
}
