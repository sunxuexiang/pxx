package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@ApiModel
@Data
public class GoodsInfoMappingVO implements Serializable {

    /**
     * 商品编号，采用UUID
     */
    @ApiModelProperty(value = "parentGoodsInfoId")
    private String parentGoodsInfoId;

    @ApiModelProperty(value = "parentGoodsInfoId")
    private String goodsInfoId;

    @ApiModelProperty(value = "parentGoodsInfoId")
    private String erpGoodsInfoNo;

    @ApiModelProperty(value = "parentGoodsInfoId")
    private Long wareId;
}
