package com.wanmi.sbc.order.api.request.trade;

import com.wanmi.sbc.common.base.Operator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/30 16:26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeAppendEventLogRequest implements Serializable {
    private static final long serialVersionUID = 5139316915787422082L;

    @NotBlank
    private String tid;

    @NotBlank
    private String desc;

    @NotBlank
    private String eventType;

    @NotNull
    private Operator operator;
}
