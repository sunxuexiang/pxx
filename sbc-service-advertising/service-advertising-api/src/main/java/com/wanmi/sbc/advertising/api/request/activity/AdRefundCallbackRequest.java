package com.wanmi.sbc.advertising.api.request.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/19 17:14
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdRefundCallbackRequest implements Serializable {

    private static final long serialVersionUID = -1597086868183751219L;

    /**
     * 退单编号
     */
    private String refundNo;

    /**
     * 状态（1：成功，2：退款中，3：失败）
     */
    private Integer refundStatus;

    /**
     * 失败时信息 回调一般建行未回传
     */
    private String failedMsg;
}
