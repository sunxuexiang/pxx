package com.wanmi.sbc.wms.requestwms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName: OrderDetails
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 18:36
 * @Version: 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WMSChargeBackDetails implements Serializable {
    private static final long serialVersionUID = 575489262639784916L;

    @ApiModelProperty(value = "上游来源备注号")
    private String referenceNo;
    @ApiModelProperty(value = "行号")
    @NotBlank
    private Integer lineNO;
    @ApiModelProperty(value = "产品")
    @NotBlank
    private String sku;

    @ApiModelProperty(value = "预期数量(最小包装数量)")
    private BigDecimal expectedQty;
    @ApiModelProperty(value = "总价")
    @NotBlank
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "批次属性01")
    @NotBlank
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
    @ApiModelProperty(value = "批次属性13")
    private String lotAtt13;
    @ApiModelProperty(value = "批次属性14")
    private String lotAtt14;
    @ApiModelProperty(value = "批次属性15")
    private String lotAtt15;
    @ApiModelProperty(value = "批次属性16")
    private String lotAtt16;
    @ApiModelProperty(value = "批次属性17")
    private String lotAtt17;
    @ApiModelProperty(value = "批次属性18")
    private String lotAtt18;
    @ApiModelProperty(value = "批次属性19")
    private String lotAtt19;
    @ApiModelProperty(value = "批次属性20")
    private String lotAtt20;
    @ApiModelProperty(value = "批次属性21")
    private String lotAtt21;
    @ApiModelProperty(value = "批次属性22")
    private String lotAtt22;
    @ApiModelProperty(value = "批次属性23")
    private String lotAtt23;
    @ApiModelProperty(value = "批次属性24")
    private String lotAtt24;
    @ApiModelProperty(value = "EDI相关信息")
    private String dedi04;
    @ApiModelProperty(value = "EDI相关信息")
    private String dedi05;
    @ApiModelProperty(value = "EDI相关信息")
    private String dedi06;
    @ApiModelProperty(value = "EDI相关信息")
    private String dedi07;
    @ApiModelProperty(value = "EDI相关信息")
    private String dedi08;

    @ApiModelProperty(value = "EDI相关信息")
    private BigDecimal dedi09;
    @ApiModelProperty(value = "EDI相关信息")
    private BigDecimal dedi10;

    @ApiModelProperty(value = "EDI相关信息")
    private String dedi11;
    @ApiModelProperty(value = "EDI相关信息")
    private String dedi12;
    @ApiModelProperty(value = "EDI相关信息")
    private String dedi13;
    @ApiModelProperty(value = "EDI相关信息")
    private String dedi14;
    @ApiModelProperty(value = "EDI相关信息")
    private String dedi15;
    @ApiModelProperty(value = "EDI相关信息")
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
}
