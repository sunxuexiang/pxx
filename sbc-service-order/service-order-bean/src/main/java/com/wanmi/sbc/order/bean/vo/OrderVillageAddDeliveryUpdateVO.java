package com.wanmi.sbc.order.bean.vo;


import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@ApiModel("更新 乡镇件订单")
@EqualsAndHashCode(callSuper = false)
public class OrderVillageAddDeliveryUpdateVO extends OrderVillageAddDeliveryVO implements Serializable {
    private static final long serialVersionUID = 1L;

}
