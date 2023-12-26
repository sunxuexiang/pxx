package com.wanmi.sbc.marketing.api.request.coupon;

import com.wanmi.sbc.marketing.bean.vo.CouponActivityVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 购买指定商品赠券关联信息request
 * @author: XinJiang
 * @time: 2022/2/14 11:47
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponActivityGoodsRequest extends CouponActivityVO {

    private static final long serialVersionUID = -674972722986993692L;

    /**
     * 活动id集合
     */
    @ApiModelProperty(value = "活动id集合")
    private List<String> activityIds;
}
