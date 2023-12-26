package com.wanmi.sbc.setting.api.request.advertising;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description: 通过id获取广告位信息请求类
 * @author: XinJiang
 * @time: 2022/2/18 14:53
 */
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisingGetByIdRequest implements Serializable {

    private static final long serialVersionUID = -823649216124605098L;

    /**
     * 首页广告位id
     */
    @ApiModelProperty(value = "首页广告位id")
    private String advertisingId;
}
