package com.wanmi.sbc.setting.api.request.videomanagement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoFollowAddRequest implements Serializable {
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String customerId;
    /**
     * 上传用户id
     */
    @ApiModelProperty(value = "上传用户id")
    private String coverFollowCustomerId;

    @ApiModelProperty(value = "店铺ID")
    private Long storeId;
}
