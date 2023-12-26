package com.wanmi.sbc.common.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author shiGuangYi
 * @createDate 2023-07-18 9:14
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImAccountCheckVO implements Serializable {
    @ApiModelProperty(value = "im需要查询的账号")
    private String UserID;


}
