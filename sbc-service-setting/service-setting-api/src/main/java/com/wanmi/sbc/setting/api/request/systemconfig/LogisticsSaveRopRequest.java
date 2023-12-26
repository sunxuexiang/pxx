package com.wanmi.sbc.setting.api.request.systemconfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by feitingting on 2019/11/7.
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsSaveRopRequest {
    /**
     * 配置ID
     */
    @ApiModelProperty(value="配置ID")
    private Long configId;

    /**
     * 快递100 api key
     */
    @ApiModelProperty(value="快递100 api key")
    private String deliveryKey;

    /**
     * 客户key
     */
    @ApiModelProperty(value="客户key")
    private String customerKey;

    /**
     * 状态 0:未启用1:已启用
     */
    @ApiModelProperty(value="0:未启用1:已启用")
    private Integer status;
}
