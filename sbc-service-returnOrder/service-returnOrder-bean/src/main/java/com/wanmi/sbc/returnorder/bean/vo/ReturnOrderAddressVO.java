package com.wanmi.sbc.returnorder.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @desc  
 * @author shiy  2023/7/11 9:25
*/
@Data
@ApiModel
public class ReturnOrderAddressVO implements Serializable {
    private static final long serialVersionUID = 4860094582115362527L;
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

    @ApiModelProperty(value = "收货人名称")
    private String receiveName;

    @ApiModelProperty(value = "收货人手机")
    private String receivePhone;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    private String detailAddress;
}
