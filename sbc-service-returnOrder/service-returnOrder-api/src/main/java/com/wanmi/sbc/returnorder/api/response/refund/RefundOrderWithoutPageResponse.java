package com.wanmi.sbc.returnorder.api.response.refund;

import com.wanmi.sbc.common.base.BaseQueryResponse;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 查询不带分页的退款单
 * Created by zhangjin on 2017/4/30.
 */
@Data
@ApiModel
public class RefundOrderWithoutPageResponse extends BaseQueryResponse<RefundOrderResponse> implements Serializable {
}
