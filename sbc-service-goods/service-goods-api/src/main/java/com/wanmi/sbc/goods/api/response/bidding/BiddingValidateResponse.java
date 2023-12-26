package com.wanmi.sbc.goods.api.response.bidding;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>竞价校验结果</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiddingValidateResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 竞价校验的结果
     */
    @ApiModelProperty(value = "竞价校验的结果")
    private List<String> validateResult;
}
