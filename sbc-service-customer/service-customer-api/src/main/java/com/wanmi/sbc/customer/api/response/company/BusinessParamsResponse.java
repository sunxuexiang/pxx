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
public class BusinessParamsResponse {

    private static final long serialVersionUID = -499117397630725286L;

    /**
     * 商家主键
     */
    @ApiModelProperty(value = "信用代码")
    @JsonProperty("RegNum")
    private String regNum;

    @ApiModelProperty(value = "公司名称")
    @JsonProperty("Name")
    private String name;
    @ApiModelProperty(value = "注册资金")
    @JsonProperty("Capital")
    private String capital;
    @ApiModelProperty(value = "法人")
    @JsonProperty("Person")
    private String person;
    @ApiModelProperty(value = "注册地址")
    @JsonProperty("Address")
    private String address;
    @ApiModelProperty(value = "经营范围")
    @JsonProperty("Business")
    private String business;
    @ApiModelProperty(value = "有效日期")
    @JsonProperty("Period")
    private String period;
    @ApiModelProperty(value = "组成形式")
    @JsonProperty("ComposingForm")
    private String composingForm;
    @ApiModelProperty(value = "成立日期")
    @JsonProperty("SetDate")
    private String setDate;
    @ApiModelProperty(value = "主体类型")
    @JsonProperty("Type")
    private String type;
    @ApiModelProperty(value = "-9102 黑白复印件告警")
    @JsonProperty("RecognizeWarnCode")
    private Integer[] recognizeWarnCode;
    @ApiModelProperty(value = "告警码说明：\n" +
            "WARN_COPY_CARD 黑白复印件告警")
    @JsonProperty("RecognizeWarnMsg")
    private String[] recognizeWarnMsg;
    @ApiModelProperty(value = "是否为副本。1为是，-1为不是。\n")
    @JsonProperty(value = "IsDuplication")
    private Integer isDuplication;
    @ApiModelProperty(value = "登记日期\n")
    @JsonProperty(value = "RegistrationDate")
    private String RegistrationDate;
    @ApiModelProperty(value = "唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。\n")
    @JsonProperty("RequestId")
    private String requestId;

    @Override
    public String toString() {
        return "BusinessParamsResponse{" +
                "regNum='" + regNum + '\'' +
                ", name='" + name + '\'' +
                ", capital='" + capital + '\'' +
                ", person='" + person + '\'' +
                ", address='" + address + '\'' +
                ", business='" + business + '\'' +
                ", period='" + period + '\'' +
                ", composingForm='" + composingForm + '\'' +
                ", setDate='" + setDate + '\'' +
                '}';
    }
}
