package com.wanmi.ares.request.replay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lm
 * @date 2022/09/17 8:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerTradeRequest {

    /*客户ID列表*/
    private List<String> customerIds;

    /*时间*/
    private String date;

}
