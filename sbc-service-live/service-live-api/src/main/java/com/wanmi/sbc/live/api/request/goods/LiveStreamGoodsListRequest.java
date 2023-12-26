package com.wanmi.sbc.live.api.request.goods;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * <p>直播商品查询参数</p>
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveStreamGoodsListRequest extends BaseQueryRequest {
    private static final long serialVersionUID = 1L;
    /**
     * 直播间id
     */
    @ApiModelProperty(value = "直播间id")
    private Integer liveRoomId;

    /**
     * 0 批发 1散批
     */
    @ApiModelProperty(value = "商品类型：0 批发 1散批")
    private Long goodsType;

    /**
     * 商品直播间上下架  0 下架 1上架
     */
    @ApiModelProperty(value = "商品直播间上下架  0 下架 1上架")
    private Long goodsStatus;

    /**
     * 模糊条件-商品名称
     */
    @ApiModelProperty(value = "模糊条件-商品名称")
    private String likeGoodsName;

    /**
     * 模糊条件-SKU编码
     */
    @ApiModelProperty(value = "模糊条件-SKU编码")
    private String likeGoodsInfoNo;
}
