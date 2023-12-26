package com.wanmi.sbc.marketing.api.request.pile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p></p>
 * author: chenchang
 * Date: 2022-09-06
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PileActivityCloseByIdRequest implements Serializable {

    private static final long serialVersionUID = 4024149997336834414L;

    /**
     * 活动id
     */
    @ApiModelProperty(value = "囤货活动id")
    @NotBlank
    private String id;
}
