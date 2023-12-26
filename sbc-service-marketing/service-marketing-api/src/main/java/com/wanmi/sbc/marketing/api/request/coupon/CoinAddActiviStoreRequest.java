package com.wanmi.sbc.marketing.api.request.coupon;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.wanmi.sbc.marketing.bean.dto.CoinActivityStoreDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoinAddActiviStoreRequest implements Serializable {

    private static final long serialVersionUID = 2978683837627342046L;
    /**
     * 营销id
     */
    @ApiModelProperty(value = "活动id")
    @NotBlank
    private String activityId;

    @ApiModelProperty(value = "订单赠金币配置的商家")
    @NotEmpty
    private List<CoinActivityStoreDTO> coinActivityStore;
}
