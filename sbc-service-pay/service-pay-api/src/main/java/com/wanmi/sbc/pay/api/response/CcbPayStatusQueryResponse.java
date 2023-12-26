package com.wanmi.sbc.pay.api.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/12/20 10:33
 */
@Data
public class CcbPayStatusQueryResponse implements Serializable {

    private static final long serialVersionUID = -2086672715299754588L;

    /**
     * 主订单编号
     */
    @JSONField(name = "Main_Ordr_No")
    private String Main_Ordr_No;

    /**
     * 订单生成时间
     */
    @JSONField(name = "Ordr_Gen_Tm")
    private String Ordr_Gen_Tm;

    /**
     * 00-成功
     * 01-失败
     */
    @JSONField(name = "Svc_Rsp_St")
    private String Svc_Rsp_St;

    /**
     * 支付流水号
     */
    @JSONField(name = "Py_Trn_No")
    private String Py_Trn_No;

    /**
     * 支付订单超时时间
     */
    @JSONField(name = "Ordr_Ovtm_Tm")
    private String Ordr_Ovtm_Tm;


    /**
     * 支付金额
     */
    @JSONField(name = "Txnamt")
    private BigDecimal Txnamt;

    /**
     * 1待支付
     * 2成功
     * 3失败
     * 4全部退款
     * 5部分退款
     * 6失效 （一直未支付，超时过期，作为失效订单，失效时间为30分钟）
     * 7 待退款
     * 8 退款失败
     * 9 待轮询
     * a 待处理
     */
    @JSONField(name = "Ordr_Stcd")
    private String Ordr_Stcd;

    /**
     * 订单编号
     */
    @JSONField(name = "Prim_Ordr_No")
    private String Prim_Ordr_No;
}
