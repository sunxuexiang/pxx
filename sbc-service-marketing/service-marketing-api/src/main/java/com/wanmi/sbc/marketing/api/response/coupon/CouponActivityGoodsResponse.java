package com.wanmi.sbc.marketing.api.response.coupon;

import com.wanmi.sbc.marketing.bean.vo.CouponActivityGoodsVO;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 购买指定商品关联商品信息
 * @author: XinJiang
 * @time: 2022/2/14 11:45
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponActivityGoodsResponse extends CouponActivityVO {

    private static final long serialVersionUID = 5441411992642743736L;

    /**
     * 关联商品list
     */
    @ApiModelProperty(value = "关联商品list")
    private List<CouponActivityGoodsVO> couponActivityGoodsVOS;
}
