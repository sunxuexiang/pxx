package com.wanmi.sbc.common.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author shiGuangYi
 * @createDate 2023-07-18 9:42
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResultVO implements Serializable {
    @ApiModelProperty(value = "状态")
    private String ActionStatus;
    @ApiModelProperty(value = "错误信息")
    private int ErrorCode;
    @ApiModelProperty(value = "错误信息")
    private String ErrorInfo;
    @ApiModelProperty(value = "返回明细")
    private List<ImResultItemVO> ResultItem;
}
