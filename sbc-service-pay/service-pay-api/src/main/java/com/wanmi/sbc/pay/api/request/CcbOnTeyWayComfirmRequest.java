package com.wanmi.sbc.pay.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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
public class CcbOnTeyWayComfirmRequest implements Serializable {


    private static final long serialVersionUID = -6498328569664105862L;

    /**
     * 支付流水号
     */
    private String pyTrnNo;

    /**
     * 分账规则
     */
    private String clrgRuleId;

    /**
     * 参与方列表
     */
    private List<CcbSubOrderParRequest> parList;
}
