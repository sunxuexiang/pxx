package com.wanmi.sbc.returnorder.returnorder.pilefsm;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 囤货退单状态机工厂类
 * @author: jiangxin
 * @create: 2021-09-28 17:33
 */
@Component
public class ReturnPileBuilderFactory implements InitializingBean {

    /**
     *
     */
    private Map<ReturnFlowState, PileBuilder> stateBuilderMap = new ConcurrentHashMap<>();

    /**
     *
     */
    @Autowired
    private List<PileBuilder> stateBuilders;

    /**
     *
     */
    @Autowired
    private BeanFactory beanFactory;

    /**
     * @param returnPileOrder
     * @return
     */
    public StateMachine<ReturnFlowState, ReturnEvent> create(ReturnPileOrder returnPileOrder) {
        ReturnFlowState flowState = returnPileOrder.getReturnFlowState();
        PileBuilder builder = stateBuilderMap.get(flowState);
        if (builder == null) {
            throw new SbcRuntimeException("K-050002",new Object[]{String.format("[退货]创建状态[%s]失败 => [未知状态]", flowState)});
        }

        //
        StateMachine<ReturnFlowState, ReturnEvent> sm;
        try {
            sm = builder.build(returnPileOrder, beanFactory);
            sm.start();
        } catch (Exception e) {
            throw new SbcRuntimeException(String.format("[退货]创建状态[%s]失败 => [%s]", flowState, e.getCause()));
        }

        //
        sm.getExtendedState().getVariables().put(ReturnPileOrder.class, returnPileOrder);
        return sm;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        stateBuilderMap = stateBuilders.stream().collect(Collectors.toMap(PileBuilder::supportState, Function.identity()));
    }
}
