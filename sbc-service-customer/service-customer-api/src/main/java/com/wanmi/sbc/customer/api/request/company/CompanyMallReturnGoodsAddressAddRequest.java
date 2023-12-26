package com.wanmi.sbc.customer.api.request.company;

import com.wanmi.sbc.customer.bean.vo.CompanyMallReturnGoodsAddressVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @program: sbc-backgroud
 * @description: aa
 * @author: gdq
 * @create: 2023-06-13 15:24
 **/
@ApiModel
@Data
public class CompanyMallReturnGoodsAddressAddRequest implements Serializable {
    private static final long serialVersionUID = 7552570854904824427L;


    @ApiModelProperty(value = "公司ID")
    @NotNull
    private Long companyInfoId;

    @ApiModelProperty(value = "省")
    private Long provinceId;

    @ApiModelProperty(value = "市")
    private Long cityId;

    @ApiModelProperty(value = "区")
    private Long areaId;

    private Long townId;

    @ApiModelProperty(value = "收货人名称")
    private String receiveName;

    @ApiModelProperty(value = "收货人手机")
    private String receivePhone;

    @NotNull
    @ApiModelProperty(value = "详细地址")
    private String detailAddress;

    @ApiModelProperty(value = "店铺Id")
    private Long storeId;
}

