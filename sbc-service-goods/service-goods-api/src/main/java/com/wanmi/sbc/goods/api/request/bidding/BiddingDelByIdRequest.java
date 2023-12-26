package com.wanmi.sbc.goods.api.request.bidding;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>单个删除竞价配置请求参数</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiddingDelByIdRequest extends GoodsBaseRequest {
private static final long serialVersionUID = 1L;

    /**
     * 竞价配置主键
     */
    @ApiModelProperty(value = "竞价配置主键")
    @NotNull
    private String biddingId;
}
