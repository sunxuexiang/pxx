package com.wanmi.sbc.returnorder.api.response.orderpicking;

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
