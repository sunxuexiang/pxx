package com.wanmi.sbc.customer.api.request.store;

import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import com.wanmi.sbc.goods.bean.enums.freightTemplateDeliveryType;
import io.swagger.annotations.ApiModel;
import lombok.*;


/**
 * <p>查询商家店铺和主账号信息request</p>
 * Created by of628-wenzhi on 2018-09-12-下午3:09.
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreDeliveryAreaRequest extends CustomerBaseRequest {
    private static final long serialVersionUID = 33282149270263463L;
    private Long id;
    private Long storeId;
    private freightTemplateDeliveryType destinationType;
    private Long wareId;
    private Long freightFreeNumber;
    private Integer openFlag;
}
