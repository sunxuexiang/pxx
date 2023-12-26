package com.wanmi.sbc.setting.api.request.villagesaddress;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * @description: 乡镇件地址配置信息查询条件请求参数实体类
 * @author: XinJiang
 * @time: 2022/4/29 11:15
 */
@Data
@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VillagesAddressConfigQueryRequest extends BaseQueryRequest {

    private static final long serialVersionUID = -4989868195579886914L;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private Long provinceId;

    private List<Long> provinceIdList;

    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    private Long cityId;

    private List<Long> cityIdList;

    /**
     * 区
     */
    @ApiModelProperty(value = "区")
    private Long areaId;

    /**
     * 街道
     */
    @ApiModelProperty(value = "街道")
    private Long villageId;

    /**
     * 街道id集合
     */
    @ApiModelProperty(value = "街道id集合")
    private List<Long> villagesIds;

    /**
     * 省名
     */
    @ApiModelProperty(value = "省名")
    private String provinceName;

    /**
     * 市名
     */
    @ApiModelProperty(value = "市名")
    private String cityName;

    /**
     * 区名
     */
    @ApiModelProperty(value = "区名")
    private String areaName;

    /**
     * 街道名
     */
    @ApiModelProperty(value = "街道名")
    private String villageName;

    /**
     * 店铺标识
     */
    @ApiModelProperty(value = "店铺标识")
    private Long storeId;

    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    @ApiModelProperty(value = "商家类型")
    private Integer companyType;

    /**
     * 删除标志
     */
    private DeleteFlag delFlag;
}
