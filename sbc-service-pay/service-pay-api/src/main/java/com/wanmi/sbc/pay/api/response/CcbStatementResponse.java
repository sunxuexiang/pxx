package com.wanmi.sbc.pay.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 建行对账单通用返回
 * @author hudong
 * 2023-09-04
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CcbStatementResponse implements Serializable {


    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;


}
