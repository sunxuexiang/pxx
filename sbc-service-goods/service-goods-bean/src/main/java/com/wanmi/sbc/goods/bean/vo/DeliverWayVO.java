package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @ClassName DeliverWayVO
 * @Description TODO
 * @Author shiy
 * @Date 2023/9/11 8:43
 * @Version 1.0
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class DeliverWayVO {

    private Integer deliveryTypeId;
    //private Integer deliverWayCode;
    private String deliverWayDesc;
}
