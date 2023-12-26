package com.wanmi.sbc.returnorder.api.response.refund;

import com.wanmi.sbc.returnorder.bean.vo.RefundOrderVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 退款单返回
 * Created by zhangjin on 2017/4/30.
 */
@Data
@ApiModel
public class RefundOrderByReturnCodeResponse extends RefundOrderVO implements Serializable{


    private static final long serialVersionUID = 7035111141390357758L;
}
