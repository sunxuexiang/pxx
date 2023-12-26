package com.wanmi.sbc.goods.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品实体类
 * Created by dyt on 2017/4/11.
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsBatchNoDTO implements Serializable {

    private static final long serialVersionUID = -8807674093765870168L;

    /**
     * 分类编号
     */
    @ApiModelProperty(value = "分类编号")
    private String goodsInfoId;

    /**
     * 货品的编号
     */
    @ApiModelProperty(value = "货品的编号")
    private String goodsInfoBatchNo;

}
