package com.wanmi.sbc.order.returnorder.newpilefsm.params;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.order.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.order.returnorder.newpilefsm.event.NewPileReturnEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 状态机请求参数
 * Created by jinwei on 21/4/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewPileReturnStateRequest {

    /**
     * 退单号
     */
    private String rid;

    /**
     * 操作人
     */
    private Operator operator;

    /**
     * 逆向事件
     */
    private NewPileReturnEvent returnEvent;

    /**
     * 参数
     */
    private Object data;
}
