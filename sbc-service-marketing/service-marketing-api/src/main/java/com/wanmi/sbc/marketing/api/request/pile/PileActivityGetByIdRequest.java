package com.wanmi.sbc.marketing.api.request.pile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p></p>
 * author: chenchang
 * Date: 2022-09-06
 */
@ApiModel
@Data
public class PileActivityGetByIdRequest implements Serializable {

    private static final long serialVersionUID = -2561155218383733831L;

    /**
     * 活动id
     */
    @ApiModelProperty(value = "囤货活动id")
    @NotBlank
    private String id;
}
