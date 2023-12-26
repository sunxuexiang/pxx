package com.wanmi.sbc.live.api.response.stream;

import com.wanmi.sbc.live.bean.vo.LiveHaveGoodsVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
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
public class LiveHaveResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 直播详情
     */
    @ApiModelProperty(value = "直播详情")
    public LiveHaveGoodsVO liveHaveGoodsVO;
}
