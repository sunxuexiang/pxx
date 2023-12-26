package com.wanmi.sbc.customer.api.response.fadada;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@ApiModel
@Data
@NoArgsConstructor
public class FadadaParamsResponese {
    private static final long serialVersionUID = -1469274484762938357L;

    @ApiModelProperty(value = "法大大对应喜吖吖系统唯一ID")
    private String customerId;

    @ApiModelProperty(value = "调用法大大绑定实名信息用")
    private String transactionNo;

    @ApiModelProperty(value = "返回填写企业的URL，前端可直接转发或者生成二维码展示")
    private String url;

    @ApiModelProperty(value = "返回法大大平台验证信息")
    private String applyCertInfo;

    public FadadaParamsResponese(String customerId,String transactionNo,String url){
        this.customerId = customerId;
        this.transactionNo = transactionNo;
        this.url = url;
    }

    public FadadaParamsResponese(String customerId,String transactionNo,String url,String applyCertInfo){
        this.customerId = customerId;
        this.transactionNo = transactionNo;
        this.url = url;
        this.applyCertInfo = applyCertInfo;
    }
}

