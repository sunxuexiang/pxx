package com.wanmi.sbc.setting.api.response.imonlineservice;

import com.wanmi.sbc.common.enums.CompanyType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
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
public class CustomerServiceStoreRelationResponse implements Serializable {

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

    /**
     * 商家名称
     */
    @ApiModelProperty(value = "商家名称")
    private String supplierName;

    /**
     * 商家类型 0、平台自营 1、第三方商家；2:统仓统配；3：零售超市；4：新散批
     */
    private CompanyType companyType;

    /**
     * 商家编号
     */
    private String companyCode;

    /**
     * 商家账号
     */
    private String contactMobile;

    /**
     * 所属批发市场
     */
    private String marketName;

    /**
     * 子店铺列表
     */
    private List<CustomerServiceStoreRelationChildResponse> childList = new ArrayList<>();

}
