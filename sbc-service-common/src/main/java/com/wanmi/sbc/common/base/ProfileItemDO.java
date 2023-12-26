package com.wanmi.sbc.common.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author shiGuangYi
 * @createDate 2023-07-18 9:59
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileItemDO {
    @ApiModelProperty(value = "标志")
    private String Tag;
    @ApiModelProperty(value = "参数")
    private String Value;
}
