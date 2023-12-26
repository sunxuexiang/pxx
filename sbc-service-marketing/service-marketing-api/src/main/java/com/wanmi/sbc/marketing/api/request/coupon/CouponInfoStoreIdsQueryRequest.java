package com.wanmi.sbc.marketing.api.request.coupon;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.CouponStatus;
import com.wanmi.sbc.marketing.bean.enums.ScopeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;


@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponInfoStoreIdsQueryRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 3288566661177085643L;
    /**
     * 批量优惠券id
     */
    @ApiModelProperty(value = "优惠券id列表")
    private List<String> storeIds;

    /**
     *  领券用户Id
     */
    @ApiModelProperty(value = "领券用户Id")
    @NotBlank
    private String customerId;

}
