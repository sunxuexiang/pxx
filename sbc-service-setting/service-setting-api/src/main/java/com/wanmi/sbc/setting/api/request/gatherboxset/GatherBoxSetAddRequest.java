package com.wanmi.sbc.setting.api.request.gatherboxset;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatherBoxSetAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 凑箱规格
     */
    @ApiModelProperty(value = "凑箱规格")
    private Long skuNum;

    @ApiModelProperty(value = "散批首页banner")
    private String banner;
}
