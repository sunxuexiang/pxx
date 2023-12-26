package com.wanmi.sbc.goods.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description: TODO
 * @author: XinJiang
 * @time: 2022/4/20 10:40
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkGoodsRecommendSortDTO implements Serializable {

    private static final long serialVersionUID = 4931542637652426553L;

    /**
     * 推荐id
     */
    @NotBlank
    @ApiModelProperty(value = "推荐id",required = true)
    private String recommendId;

    /**
     * 排序顺序
     */
    @NotNull
    @ApiModelProperty(value = "排序顺序",required = true)
    private Integer sortNum;
}
