package com.wanmi.sbc.customer.api.response.distribution;

import com.wanmi.sbc.customer.bean.vo.DistributionCustomerVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>依据分销员id验证分销员状态response</p>
 * @author lq
 * @date 2019-02-19 10:13:15
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionCustomerEnableByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 分销状态正常
     */
    @ApiModelProperty(value = "分销状态是否正常")
    private Boolean distributionEnable;
}
