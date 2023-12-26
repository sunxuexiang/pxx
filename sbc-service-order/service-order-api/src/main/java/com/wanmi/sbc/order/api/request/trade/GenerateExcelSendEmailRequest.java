package com.wanmi.sbc.order.api.request.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class GenerateExcelSendEmailRequest implements Serializable {
    private static final long serialVersionUID = 5454453055876550198L;

    /**
     * 附件内容
     */
    @ApiModelProperty(value = "附件内容")
    ByteArrayInputStream byteArrayInputStream;

    /**
     * 邮件收件人邮箱，支持多个收件人邮箱
     */
    @ApiModelProperty(value = "邮件收件人邮箱")
    private List<String> acceptAddressList;

    /**
     * 邮件的标题
     */
    @ApiModelProperty(value = "邮件的标题")
    private String emailTitle;

    /**
     * 邮件内容
     */
    @ApiModelProperty(value = "邮件内容")
    private String emailContent;
}
