package com.wanmi.sbc.customer.api.request.fadada;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@ApiModel
@Data
@Component
public class FadadaParamsRequest {
    private static final long serialVersionUID = -1469274484762938357L;

    @ApiModelProperty(value = "法大大对应喜吖吖系统唯一ID")
    private String customerId;

    @ApiModelProperty(value = "交易流水号,调用法大大绑定实名信息用")
    private String transactionNo;

    @ApiModelProperty(value = "企业类型，1.个人，2.企业")
    @Value(value = "2")
    private String accountType;

    @ApiModelProperty(value = "企业类型，1.个人，2.企业 这是保存于xyy自己数据库")
    private Integer isPerson;

    @ApiModelProperty(value = "上传合同的路径")
    private String contractUrl;

    @ApiModelProperty(value = "上传合同标题")
    private String docTitle;

    @ApiModelProperty(value = "是否启用标识。0未启用，1启用")
    private int contractFlag;

    @ApiModelProperty(value = "线下签署合同上传来的照片(多个用逗号分隔)")
    private String imgUrl;

    @ApiModelProperty(value = "0: 线上。1:线下")
    private Integer signType;

    @ApiModelProperty(value = "商户名称")
    private String supplierName;

    @ApiModelProperty(value = "招商经理")
    private String investmentManager;

    @ApiModelProperty(value = "招商经理ID")
    private String investemntManagerId;

    @ApiModelProperty(value = "员工ID")
    private String employeeId;

    @ApiModelProperty(value = "合同ID")
    private String userContractId;

    @ApiModelProperty(value = "公司ID")
    private Long companyInfoId;



}

