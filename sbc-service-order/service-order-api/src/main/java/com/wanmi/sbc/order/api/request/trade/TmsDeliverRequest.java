package com.wanmi.sbc.order.api.request.trade;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.order.bean.dto.LogisticsDTO;
import com.wanmi.sbc.order.bean.dto.ShippingItemDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc  
 * @author shiy  2023/11/8 15:44
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TmsDeliverRequest implements Serializable {
    @ApiModelProperty(value = "订单信息")
    @NotNull
    private String tid;

    @ApiModelProperty(value = "物流信息")
    @NotNull
    private LogisticsDTO logistics;

    @ApiModelProperty(value = "发货清单")
    private List<ShippingItemDTO> shippingItems = new ArrayList<>();

    @ApiModelProperty(value = "赠品信息")
    private List<ShippingItemDTO> giftItemList = new ArrayList<>();

    @ApiModelProperty(value = "发货时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime deliverTime;

    @ApiModelProperty(value = "操作人")
    @NotNull
    private String operatorName;
    public Operator getOperator(){
        if(operatorName==null) {
            operatorName = "system";
        }
        return Operator.builder().platform(Platform.THIRD).name(operatorName).adminId("123456").userId("123456").ip("127.0.0.1").storeId("0").companyType(CompanyType.PLATFORM.toValue())
                .account("123456").companyInfoId(0L).build();
    }
}
