package com.wanmi.sbc.returnorder.api.request.purchase;

import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.returnorder.bean.dto.PurchaseQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-12-03
 */
@Data
@ApiModel
public class PurchaseListRequest extends PurchaseQueryDTO {

    private static final long serialVersionUID = -454248433046596178L;

    /**
     * 登录用户
     */
    @ApiModelProperty(value = "登录用户")
    private CustomerVO customer;

    /**
     * 是否初始化
     */
    @ApiModelProperty(value = "是否初始化")
    private Boolean isRefresh = Boolean.FALSE;

    /**
     * 是否分页
     */
    @ApiModelProperty(value = "是否分页")
    private Boolean isPage = Boolean.TRUE;

    /**
     * 客户收货地址省
     */
    @ApiModelProperty(value = "省")
    private Long provinceId;

    /**
     * 客户收货地址市
     */
    @ApiModelProperty(value = "市")
    private Long cityId;


    /**
     * 定位地址省
     */
    @ApiModelProperty(value = "省")
    private Long locationProvinceId;

    /**
     * 定位地址市
     */
    @ApiModelProperty(value = "市")
    private Long locationCityId;
}
