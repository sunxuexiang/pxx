package com.wanmi.sbc.goods.bean.vo;

import com.wanmi.sbc.common.annotation.CanEmpty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品实体类
 * Created by dyt on 2017/4/11.
 */
@ApiModel
@Data
public class SettingGoodsVO implements Serializable {

    private static final long serialVersionUID = 2757888812286445293L;

    /**
     * 商品主图
     */
    @ApiModelProperty(value = "商品主图")
    @CanEmpty
    private String goodsImg;

    /**
     * 商品销量
     */
    @ApiModelProperty(value = "商品销量")
    private Long goodsSalesNum;

}
