package com.wanmi.sbc.live.api.request.goods;

import com.wanmi.sbc.live.bean.vo.LiveStreamActivityVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamGoodsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * <p>直播活动查询参数</p>
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveStreamGoodsModifyRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "直播商品goodsInfoId")
    private String goodsInfoId;
    /**
     * 多个直播商品
     */
    @ApiModelProperty(value = "多个直播商品")
    private List<String> goodsInfoIds;

    @ApiModelProperty(value = "直播商品推荐状态")
    private Integer explainFlag;
    /**
     * 直播间id
     */
    @ApiModelProperty(value = "直播间id")
    @NonNull
    private Integer liveRoomId;
    /**
     * 商品直播间上下架  0 下架 1上架
     */
    @ApiModelProperty(value = "商品直播间上下架  0 下架 1上架")
    private Long goodsStatus;

    /**
     * 直播间商品删除标识,0:未删除1:已删除
     */
    @ApiModelProperty(value = "直播间商品删除标识,0:未删除1:已删除")
    private Integer delFlag;
}
