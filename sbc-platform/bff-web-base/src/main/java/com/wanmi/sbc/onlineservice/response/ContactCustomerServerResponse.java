package com.wanmi.sbc.onlineservice.response;

import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceListResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>onlineService座席列表结果</p>
 * * @Author shiGuangYi
 * @createDate 2023-06-05 17:30
 * @Description: 腾讯客服  IM
 * @Version 1.0
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactCustomerServerResponse extends ImOnlineServiceListResponse {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "店铺信息")
    private StoreCommonInfoResponse storeVO;

}
