package com.wanmi.sbc.setting.api.request.advertising;

import com.wanmi.sbc.setting.bean.enums.AdvertisingRetailJumpType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description: 校验分类id或者商品erp编码是否有效请求类
 * @author: XinJiang
 * @time: 2022/4/19 14:22
 */
@Data
@ApiModel
public class CheckCateIdOrErpNoRequest implements Serializable {

    private static final long serialVersionUID = 1333024973729781872L;

    /**
     * 跳转类型
     */
    @ApiModelProperty(value = "跳转类型 0：分类，1：商品")
    @NotNull
    private AdvertisingRetailJumpType jumpType;

    /**
     * 跳转编码 分类id/商品erp编码
     */
    @NotBlank
    @ApiModelProperty(value = "跳转编码 分类id/商品erp编码")
    private String jumpCode;
}
