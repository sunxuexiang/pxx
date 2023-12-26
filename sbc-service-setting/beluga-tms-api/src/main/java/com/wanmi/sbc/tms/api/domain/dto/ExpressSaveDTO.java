package com.wanmi.sbc.tms.api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 快递到家-运单保存结果DTO
 * @author jkp
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpressSaveDTO implements Serializable {

    /**
     * 运单信息
     */
    private String expressOrderId;

    /**
     * 交易订单信息
     */
    private String tradeOrderId;
}
