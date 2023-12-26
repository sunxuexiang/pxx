package com.wanmi.sbc.order.historytownshiporder.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrueStock implements Serializable {

    private static final long serialVersionUID = -8005242411369818186L;


    private String skuid;
    private BigDecimal stock;
}
