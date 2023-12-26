package com.wanmi.sbc.marketing.api.request.coupon;

import com.wanmi.sbc.marketing.bean.dto.GoodsInfoDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author Administrator
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoinAddActivitGoodsRequest implements Serializable {

    private static final long serialVersionUID = 2978683837627342046L;
    /**
     * 营销id
     */
    @ApiModelProperty(value = "活动id")
    @NotBlank
    private String activityId;

    /**
     * 商品ID
     */
    @ApiModelProperty(value = "商品信息")
    private List<GoodsInfoDTO> goodsInfos;
}
