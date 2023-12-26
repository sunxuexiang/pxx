package com.wanmi.ares.request.replay;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-10-11 10:06
 **/
@Data
public class ReplayTradeBuyerStoreQuery implements Serializable {

    private static final long serialVersionUID = 8725241589113213906L;

    private Date startTime;

    // 1:store ,2: buyer,store
    private Integer type;
}
