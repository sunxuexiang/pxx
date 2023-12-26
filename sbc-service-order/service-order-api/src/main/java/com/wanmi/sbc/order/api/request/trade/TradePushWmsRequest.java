package com.wanmi.sbc.order.api.request.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: yinxianzhi
 * @Date: Created In 下午3:28 2019/5/20
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradePushWmsRequest implements Serializable {

    @ApiModelProperty("查询起始时间")
    private LocalDateTime startTime;

    private List<String> tidList;

}
