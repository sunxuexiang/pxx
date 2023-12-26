package com.wanmi.sbc.pay.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/19 11:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CcbArrivalRequest implements Serializable {

    private static final long serialVersionUID = 455656162576801309L;


    /**
     * 主订单编号
     */
    private String primOrdrNo;

    /**
     * 子订单编号
     */
    private String subOrdrId;
}
