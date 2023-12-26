package com.wanmi.sbc.live.api.response.goods;

import com.wanmi.sbc.live.bean.vo.LiveStreamGoodsVO;
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
public class LiveStreamGoodsListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 直播商品列表结果
     */
    @ApiModelProperty(value = "直播商品list")
    private List<LiveStreamGoodsVO> liveStreamGoodsVO;
}
