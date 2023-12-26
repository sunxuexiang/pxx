package com.wanmi.sbc.customer.api.response.baiduBean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetDistance {
    //2个经纬度的距离
    private double distanceResult;
}
