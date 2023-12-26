package com.wanmi.sbc.returnorder.api.request.refund;


import com.wanmi.sbc.returnorder.bean.dto.RefundOrderRequestDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;


/**
 * 退款单
 * Created by zhangjin on 2017/4/21.
 */
@Data
@ApiModel
public class RefundOrderPageRequest extends RefundOrderRequestDTO {

    private static final long serialVersionUID = -5620693206009388482L;
}
