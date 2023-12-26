package com.wanmi.sbc.order.api.response.orderpicking;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderPickingListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    List<OrderPickingResponse> orderPickingResponseList;
}
