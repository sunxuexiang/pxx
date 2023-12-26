package com.wanmi.sbc.returnorder.api.response.refund;

import com.wanmi.sbc.common.base.BaseQueryResponse;
import io.swagger.annotations.ApiModel;
import com.wanmi.sbc.returnorder.bean.vo.RefundOrderResponse;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页返回
 * Created by zhangjin on 2017/4/30.
 */
@Data
@ApiModel
public class RefundOrderPageResponse extends BaseQueryResponse<RefundOrderResponse> implements Serializable {
}
