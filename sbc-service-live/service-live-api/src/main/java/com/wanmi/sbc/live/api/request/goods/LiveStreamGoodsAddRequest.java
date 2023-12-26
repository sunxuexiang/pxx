package com.wanmi.sbc.live.api.request.goods;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.live.bean.vo.LiveStreamGoodsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * <p>直播商品新增参数</p>
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveStreamGoodsAddRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;
    /**
     * 商品列表
     */
    @ApiModelProperty(value = "直播商品保存list")
    private List<String> goodsInfoIds;

    /**
     * 直播间id
     */
    @ApiModelProperty(value = "直播间id")
    private Integer liveRoomId;

    /**
     * 0 批发 1散批
     */
    @ApiModelProperty(value = "0 批发 1散批")
    private Long goodsType;
}
