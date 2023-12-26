package com.wanmi.sbc.marketing.bean.vo;

import com.wanmi.sbc.goods.bean.vo.GoodsInfoResponseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-19 15:57
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingForEndVO extends MarketingVO {

    private static final long serialVersionUID = 1268667127237009997L;
    /**
     * 满减等级
     */
    @ApiModelProperty(value = "营销满减多级优惠列表")
    private List<MarketingFullReductionLevelVO> fullReductionLevelList;

    /**
     * 满折等级
     */
    @ApiModelProperty(value = "营销满折多级优惠列表")
    private List<MarketingFullDiscountLevelVO> fullDiscountLevelList;

    /**
     * 满赠等级
     */
    @ApiModelProperty(value = "营销满赠多级优惠列表")
    private List<MarketingFullGiftLevelVO> fullGiftLevelList;

    /**
     * 营销关联商品
     */
    @ApiModelProperty(value = "营销关联商品信息")
    private GoodsInfoResponseVO goodsList;

    @Override
    public List<Long> getJoinLevelList() {
        return Arrays.stream(getJoinLevel().split(",")).map(Long::parseLong).collect(Collectors.toList());
    }

    @ApiModelProperty(value = "是否显示后端状态")
    private Boolean isShowActiveStatus =Boolean.FALSE;

}
