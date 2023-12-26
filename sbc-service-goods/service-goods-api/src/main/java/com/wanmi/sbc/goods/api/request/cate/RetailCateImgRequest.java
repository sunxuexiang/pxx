package com.wanmi.sbc.goods.api.request.cate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 零售分类图标请求类
 * @author: XinJiang
 * @time: 2022/3/29 16:45
 */
@ApiModel
@Data
public class RetailCateImgRequest implements Serializable {

    private static final long serialVersionUID = -6416761989884540555L;

    /**
     * 图片地址
     */
    @ApiModelProperty(value = "图片地址")
    private String imgUrl;
}
