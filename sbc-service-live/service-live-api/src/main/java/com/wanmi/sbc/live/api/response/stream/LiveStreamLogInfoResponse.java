package com.wanmi.sbc.live.api.response.stream;

import com.wanmi.sbc.live.bean.vo.LiveStreamLogInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveStreamLogInfoResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 直播记录商品，优惠劵活动id
     */
    @ApiModelProperty(value = "直播记录商品，优惠劵活动id")
    private LiveStreamLogInfoVO liveStreamLogInfoVO;
}
