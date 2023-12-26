package com.wanmi.sbc.customer.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel
public class CompanyMallGoodsAddressVO implements Serializable {
    private static final long serialVersionUID = 4860094582115362527L;
    /**
     * 退货地址Id
     */
    @ApiModelProperty(value = "Id")
    private Long Id;

    /**
     * 市场编号
     */
    @ApiModelProperty(value = "商家Id")
    private Long companyInfoId;


    @ApiModelProperty(value = "店铺Id")
    private Long storeId;



    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private Long provinceId;

    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    private Long cityId;

    @ApiModelProperty(value = "区")
    private Long areaId;

    private Long townId;

    @ApiModelProperty(value = "收货人名称")
    private String receiveName;

    @ApiModelProperty(value = "收货人手机")
    private String receivePhone;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    private String detailAddress;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private String operator;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

    /**
     * 删除标志 0未删除 1已删除
     */
    @ApiModelProperty(value = "删除标志 0未删除 1已删除")
    private Integer delFlag;

}
