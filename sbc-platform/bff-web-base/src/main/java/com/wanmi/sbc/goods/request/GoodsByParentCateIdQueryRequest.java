package com.wanmi.sbc.goods.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class GoodsByParentCateIdQueryRequest extends BaseQueryRequest {

    /**
     * 分类编号
     */
    @ApiModelProperty(value = "分类编号")
    private Long cateId;

    /**
     * 分类编号集合
     */
    @ApiModelProperty(value = "分类编号集合")
    private List<Long> cateIds;

    @ApiModelProperty(value = "店铺分类编号集合")
    private List<Long> storeCateIds;

    @ApiModelProperty(value = "店铺分类编号")
    private Long storeCatId;

    /**
     * 是否能匹配仓
     */
    @ApiModelProperty(value = "是否能匹配仓")
    private Boolean matchWareHouseFlag;

    /**
     * 匹配的分仓Id
     */
    @ApiModelProperty(value = "匹配的分仓Id")
    private Long wareId;

    @ApiModelProperty(value = "是否根据品类绑定的品牌排序")
    private Boolean sortByCateBrand = false;

    @ApiModelProperty(value = "商品分类，0散称，1定量, 不传查询全部")
    private Integer isScatteredQuantitative;

    /**
     * 批量品牌编号
     */
    @ApiModelProperty(value = "批量品牌编号")
    private List<Long> brandIds;

    private Long storeId;

    @ApiModelProperty(value = "排序标识:/**\n" +
            "     * 排序标识\n" +
            "     * 0: 销量倒序->时间倒序->市场价倒序\n" +
            "     * 1:上架时间倒序->销量倒序->市场价倒序\n" +
            "     * 2:市场价倒序->销量倒序\n" +
            "     * 3:市场价升序->销量倒序\n" +
            "     * 4:销量倒序->市场价倒序\n" +
            "     * 5:评论数倒序->销量倒序->市场价倒序\n" +
            "     * 6:好评倒序->销量倒序->市场价倒序\n" +
            "     * 7:收藏倒序->销量倒序->市场价倒序\n" +
            "     */")
    private Integer sortFlag;
}
