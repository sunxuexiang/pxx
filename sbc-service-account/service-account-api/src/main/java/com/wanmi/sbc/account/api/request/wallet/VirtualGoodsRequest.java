package com.wanmi.sbc.account.api.request.wallet;

import com.wanmi.sbc.account.bean.vo.VirtualGoodsVO;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityConfigSaveRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author jeffrey
 * @create 2021-08-21 9:45
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel
public class VirtualGoodsRequest extends BaseQueryRequest {

    @ApiModelProperty(value = "虚拟商品")
    private VirtualGoodsVO virtualGoodsVO;

    @ApiModelProperty(value = "商品ID")
    private Long goodsId;

    @ApiModelProperty(value = "多个虚拟商品id")
    private List<Long> goodsIdList;


    @ApiModelProperty(value = "删除标识")
    private Integer delFlag ;

    @ApiModelProperty(value = "操作人")
    private String createPerson;

    @ApiModelProperty(value = "优惠券活动配置信息")
    @Size(min = 1, max = 10)
    private List<CouponActivityConfigSaveRequest> couponActivityConfigs;
}
