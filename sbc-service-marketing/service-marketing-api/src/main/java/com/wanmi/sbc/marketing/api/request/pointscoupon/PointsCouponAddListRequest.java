package com.wanmi.sbc.marketing.api.request.pointscoupon;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yang
 * @since 2019/5/14
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsCouponAddListRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "积分优惠券信息")
    List<PointsCouponAddRequest> pointsCouponAddRequestList;

    /**
     * 兑换开始时间
     */
    @ApiModelProperty(value = "兑换开始时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime beginTime;

    /**
     * 兑换结束时间
     */
    @ApiModelProperty(value = "兑换结束时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;
}
