package com.wanmi.ares.request.screen;

import com.wanmi.ares.request.screen.model.ScreenOrder;
import com.wanmi.ares.request.screen.model.ScreenOrderDetail;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lm
 * @date 2022/09/24 10:10
 */
@Data
public class GenerateOrderRequest implements Serializable {

    private List<ScreenOrder> screenOrders;

    private List<ScreenOrderDetail> screenOrderDetails;
}
