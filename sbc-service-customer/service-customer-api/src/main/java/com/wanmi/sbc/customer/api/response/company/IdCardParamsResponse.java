package com.wanmi.sbc.customer.api.response.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdCardParamsResponse {

    private static final long serialVersionUID = -499117397630725286L;

    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "性别")
    private String sex;
    @ApiModelProperty(value = "民族")
    private String nation;
    @ApiModelProperty(value = "出生日期")
    private String birth;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "身份证号")
    private String idNum;

}
