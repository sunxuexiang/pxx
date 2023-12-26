package com.wanmi.sbc.goods.api.request.brand;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandRecommendVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsBrandRecommendRequest extends BaseQueryRequest {

    /**
     * 品牌推荐id
     */
    @ApiModelProperty(value = "品牌推荐id")
    private Long goodsBrandRecommendId;

    /**
     * 品牌id
     */
    @ApiModelProperty(value = "品牌id")
    private Long brandId;

    /**
     * 上下架的时间
     */
    @ApiModelProperty(value = "上下架的时间")
    private LocalDateTime addedTime;


    /**
     * 上下架状态,0:下架1:上架
     */
    @ApiModelProperty(value = "上下架状态")
    private Integer addedFlag;


    /**
     * 删除标识,0:未删除1:已删除
     */
    @ApiModelProperty(value = "删除标识")
    private Integer delFlag;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 分类名称显示状态：0不显示，1显示
     */
    @ApiModelProperty(value = "分类名称显示状态：0不显示，1显示")
    private Integer nameStatus;

    /**
     * 品牌推荐列表
     */
    @ApiModelProperty(value = "品牌推荐列表")
    private List<GoodsBrandRecommendVO> goodsBrandRecommendVOList;

    /**
     * 品牌推荐新增
     */
    @ApiModelProperty(value = "品牌推荐新增")
    private List<GoodsBrandRecommendVO> addGoodsBrandRecommendVOList;

    /**
     * 品牌推荐上下架
     */
    @ApiModelProperty(value = "品牌推荐上下架")
    private List<Long> updateAddedByGoodsBrandRecommendIdList;

    /**
     * 品牌推荐删除
     */
    @ApiModelProperty(value = "品牌推荐删除")
    private List<Long> deleteByGoodsBrandRecommendIdList;
}
