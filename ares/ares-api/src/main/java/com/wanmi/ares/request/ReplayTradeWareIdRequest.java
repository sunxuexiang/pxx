package com.wanmi.ares.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author lm
 * @date 2022/11/30 17:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public  class ReplayTradeWareIdRequest implements Serializable {

    private List<Long> wareIds;
}