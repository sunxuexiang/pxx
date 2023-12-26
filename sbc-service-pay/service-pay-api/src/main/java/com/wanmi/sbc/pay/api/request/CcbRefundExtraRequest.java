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
 * @create 2023/11/19 15:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CcbRefundExtraRequest implements Serializable {

    private static final long serialVersionUID = -2149661005181715385L;

    String tid;

    String rid;

    String payOrderNo;
}
