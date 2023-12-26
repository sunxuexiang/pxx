package com.wanmi.sbc.marketing.api.request.coupon;

import com.wanmi.sbc.marketing.bean.vo.CouponActivityVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class LongNotOrderSendCouponGroupRequest implements Serializable {

    private static final long serialVersionUID = -6391420048468987728L;


    @ApiModelProperty(value = "客户id")
    @NotNull
    private String customerId;

    @ApiModelProperty(value = "活动信息")
    @NotNull
    private List<CouponActivityVO> activitys;
}
