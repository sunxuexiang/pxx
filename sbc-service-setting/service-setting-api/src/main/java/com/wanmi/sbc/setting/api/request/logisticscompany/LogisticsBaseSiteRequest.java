package com.wanmi.sbc.setting.api.request.logisticscompany;

import com.wanmi.sbc.setting.bean.vo.LogisticsBaseSiteVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @desc  物流线路
 * @author shiy  2023/11/7 9:43
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsBaseSiteRequest  implements Serializable {

    /**
     * site_id
     */
    @ApiModelProperty("site_id")
    private Long siteId;

    /**
     * 物流公司编号
     */
    @ApiModelProperty("物流公司编号")
    private Long logisticsId;

    /**
     * 站点名
     */
    @ApiModelProperty("站点名")
    private String siteName;

    /**
     * 站点人
     */
    @ApiModelProperty("站点人")
    private String sitePerson;

    /**
     * 站点电话
     */
    @ApiModelProperty("站点电话")
    private String sitePhone;

    private Integer siteCrtType;

    /**
     * 区域ID
     */
    @ApiModelProperty("区域ID")
    private Long baseAddressId;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    private String createPerson;

}