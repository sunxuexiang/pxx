package com.wanmi.sbc.live.api.response.stream;

import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
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
public class LiveStreamInfoResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 直播详情
     */
    @ApiModelProperty(value = "直播详情")
    public LiveStreamVO content;

    /**
     * 直播分享用户
     */
    @ApiModelProperty(value = "直播分享用户")
    public CustomerVO sharer;
}
