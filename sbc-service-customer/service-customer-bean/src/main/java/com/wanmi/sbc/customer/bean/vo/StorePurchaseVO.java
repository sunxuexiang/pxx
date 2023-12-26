package com.wanmi.sbc.customer.bean.vo;

import com.wanmi.sbc.common.enums.CompanyType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 店铺信息
 * Created by yang on 2020/12/29.
 */
@ApiModel
@Data
public class StorePurchaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 店铺主键
     */
    @ApiModelProperty(value = "店铺主键")
    private Long storeId;

    /**
     * 公司信息
     */
   /* @ApiModelProperty(value = "公司信息")
    private CompanyInfoVO companyInfo;*/

    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    @ApiModelProperty(value = "商家类型(0、平台自营 1、第三方商家)")
    private CompanyType companyType;

    /**
     * 一对多关系，多个SPU编号
     */
    @ApiModelProperty(value = "多个SPU编号")
    private List<String> goodsIds = new ArrayList<>();

}
