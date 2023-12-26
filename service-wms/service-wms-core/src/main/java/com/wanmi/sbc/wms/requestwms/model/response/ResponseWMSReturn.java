package com.wanmi.sbc.wms.requestwms.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: ResponseWMSReturn
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 19:36
 * @Version: 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWMSReturn implements Serializable {


    private static final long serialVersionUID = 4089990012651415650L;

    @ApiModelProperty(value = "返回code")
    private String returnCode;
    @ApiModelProperty(value = "返回信息描述")
    private String returnDesc;
    @ApiModelProperty(value = "返回标志位")
    private Integer returnFlag;
    @ApiModelProperty(value = "返回描述")
    private String returnInfo;
}
