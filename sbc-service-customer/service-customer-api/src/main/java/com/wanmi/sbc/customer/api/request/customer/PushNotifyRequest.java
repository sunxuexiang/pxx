package com.wanmi.sbc.customer.api.request.customer;

import com.wanmi.sbc.common.util.tencent.TencentImMessageType;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PushNotifyRequest implements Serializable {

    private TencentImMessageType tencentImMessageType;

}
