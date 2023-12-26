package com.wanmi.sbc.goods.api.request.retailgoodscommend;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 通过id移除鲸喜推荐商品信息请求参数类
 * @author: XinJiang
 * @time: 2022/4/20 11:26
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetailGoodsRecommendDelByIdRequest implements Serializable {

    private static final long serialVersionUID = -3839048473439936381L;

    /**
     * 推荐id
     */
    @ApiModelProperty(value = "推荐id，单个移除时使用")
    private String recommendId;

    /**
     * 推荐id集合
     */
    @ApiModelProperty(value = "推荐id集合，批量移除时使用")
    private List<String> recommendIdList;
}
