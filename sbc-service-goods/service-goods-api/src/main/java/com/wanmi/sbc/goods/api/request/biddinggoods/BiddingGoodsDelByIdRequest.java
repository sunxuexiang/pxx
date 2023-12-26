package com.wanmi.sbc.goods.api.request.biddinggoods;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>单个删除竞价商品请求参数</p>
 * @author baijz
 * @date 2020-08-05 16:34:44
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiddingGoodsDelByIdRequest extends GoodsBaseRequest {
private static final long serialVersionUID = 1L;

    /**
     * 竞价商品的Id
     */
    @ApiModelProperty(value = "竞价商品的Id")
    @NotNull
    private String biddingGoodsId;
}
