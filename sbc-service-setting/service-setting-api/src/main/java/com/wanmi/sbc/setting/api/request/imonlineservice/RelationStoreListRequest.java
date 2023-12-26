package com.wanmi.sbc.setting.api.request.imonlineservice;


import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.CompanyType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel
@Data
public class RelationStoreListRequest extends BaseQueryRequest implements Serializable {

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺ID
     */
    private Long storeId;

    @ApiModelProperty(value = "公司类型 0、平台自营 1、第三方商家 2、统仓统配 3、零售超市 4、新散批")
    private Integer companyType;

    /**
     * 商家编号
     */
    @ApiModelProperty(value = "商家编号")
    private String companyCode;

    /**
     * 商家名称
     */
    @ApiModelProperty(value = "商家名称")
    private String supplierName;

    /**
     * 商家账号
     */
    private String contactMobile;

}
