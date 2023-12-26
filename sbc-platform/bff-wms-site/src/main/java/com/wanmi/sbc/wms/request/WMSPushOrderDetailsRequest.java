package com.wanmi.sbc.wms.request;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName: SalesOrderDetails
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/8 14:40
 * @Version: 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WMSPushOrderDetailsRequest implements Serializable {
    private static final long serialVersionUID = -884962266701057125L;
    @ApiModelProperty(value = "上游来源备注号")
    private String referenceNo;
    @ApiModelProperty(value = "行号")
    private Integer lineNo;
    @ApiModelProperty(value = "产品")
    
    @JSONField(name = "sku")
    private String sku;
    @ApiModelProperty(value = "订货数")
    
    private Long qtyOrdered;
    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "批次属性01")
    private String lotAtt01;

    @ApiModelProperty(value = "批次属性02")
    private String lotAtt02;
    @ApiModelProperty(value = "批次属性03")
    private String lotAtt03;
    @ApiModelProperty(value = "批次属性04")
    private String lotAtt04;
    @ApiModelProperty(value = "批次属性05")
    private String lotAtt05;
    @ApiModelProperty(value = "批次属性06")
    private String lotAtt06;
    @ApiModelProperty(value = "批次属性07")
    private String lotAtt07;
    @ApiModelProperty(value = "批次属性08")
    private String lotAtt08;
    @ApiModelProperty(value = "批次属性09")
    private String lotAtt09;
    @ApiModelProperty(value = "批次属性10")
    private String lotAtt10;
    @ApiModelProperty(value = "批次属性11")
    private String lotAtt11;
    @ApiModelProperty(value = "批次属性12")
    private String lotAtt12;

    @ApiModelProperty(value = "EDI信息")
    private String dedi04;
    @ApiModelProperty(value = "EDI信息")
    private String dedi05;
    @ApiModelProperty(value = "EDI信息")
    private String dedi06;
    @ApiModelProperty(value = "EDI信息")
    private String dedi07;
    @ApiModelProperty(value = "EDI信息")
    private String dedi08;
    @ApiModelProperty(value = "EDI信息")
    private String dedi09;
    @ApiModelProperty(value = "EDI信息")
    private String dedi10;
    @ApiModelProperty(value = "EDI信息")
    private String dedi11;
    @ApiModelProperty(value = "EDI信息")
    private String dedi12;
    @ApiModelProperty(value = "EDI信息")
    private String dedi13;
    @ApiModelProperty(value = "EDI信息")
    private String dedi14;
    @ApiModelProperty(value = "EDI信息")
    private String dedi15;
    @ApiModelProperty(value = "EDI信息")
    private String dedi16;
    /**
     * 是否为特价的商品的标识：0：正常商品，1：特价商品
     */
    @ApiModelProperty(value = "用户自定义1")
    private String userDefine1;
    @ApiModelProperty(value = "用户自定义2")
    private String userDefine2;
    @ApiModelProperty(value = "用户自定义3")
    private String userDefine3;
    @ApiModelProperty(value = "用户自定义4")
    private String userDefine4;
    @ApiModelProperty(value = "用户自定义5")
    private String userDefine5;
    @ApiModelProperty(value = "用户自定义6")
    private String userDefine6;
    @ApiModelProperty(value = "备注")
    private String notes;

    @ApiModelProperty(value = "发货数")
    private BigDecimal qtyShipped;


}
