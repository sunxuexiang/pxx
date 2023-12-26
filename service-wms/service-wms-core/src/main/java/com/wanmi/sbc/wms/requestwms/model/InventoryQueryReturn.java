package com.wanmi.sbc.wms.requestwms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName: InventoryQueryResponse
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 17:43
 * @Version: 1.0
 */
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class InventoryQueryReturn implements Serializable {


    private static final long serialVersionUID = -4460907459490071279L;
    @ApiModelProperty(value = "仓库ID")
    private String warehouseId;
    @ApiModelProperty(value = "货主ID")
    private String customerId;
    @ApiModelProperty(value = "产品")
    private String sku;
    @ApiModelProperty(value = "可用数量")
    private BigDecimal qty;
    @ApiModelProperty(value = "预留字段")
    private String lotatt01;
    @ApiModelProperty(value = "预留字段")
    private String lotatt02;
    @ApiModelProperty(value = "预留字段")
    private String lotatt03;
    @ApiModelProperty(value = "预留字段")
    private String lotatt04;
    @ApiModelProperty(value = "预留字段")
    private String lotatt05;
    @ApiModelProperty(value = "预留字段")
    private String lotatt06;
    @ApiModelProperty(value = "预留字段")
    private String lotatt07;
    @ApiModelProperty(value = "预留字段")
    private String lotatt08;
    @ApiModelProperty(value = "预留字段")
    private String lotatt09;
    @ApiModelProperty(value = "预留字段")
    private String lotatt010;
    @ApiModelProperty(value = "预留字段")
    private String lotatt011;
    @ApiModelProperty(value = "预留字段")
    private String lotatt012;
    @ApiModelProperty(value = "冻结数量")
    private String userDefine1;
    @ApiModelProperty(value = "分配数量")
    private String userDefine2;
    @ApiModelProperty(value = "预留字段")
    private String userDefine3;
    @ApiModelProperty(value = "预留字段")
    private String userDefine4;
    @ApiModelProperty(value = "预留字段")
    private String userDefine5;


    /**
     * 数量
     */
    private BigDecimal stockNum;


}
