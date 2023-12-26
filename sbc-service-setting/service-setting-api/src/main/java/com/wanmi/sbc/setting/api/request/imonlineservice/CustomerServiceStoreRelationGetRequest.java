package com.wanmi.sbc.setting.api.request.imonlineservice;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>在线客服店铺关系</p>
 * @author zzg
 * @date 2023-10-05 16:10:28
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceStoreRelationGetRequest extends BaseQueryRequest {

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 公司ID
     */
    private Long companyInfoId;

    /**
     * 店铺名字
     */
    private String storeName;


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

    private List<Long> storeIds;

    private List<Long> companyInfoIds;
}
