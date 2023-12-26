package com.wanmi.sbc.setting.api.request.advertising;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description: 删除请求参数类类
 * @author: XinJiang
 * @time: 2022/2/18 14:28
 */
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisingDelByIdRequest implements Serializable {

    private static final long serialVersionUID = -6844801672997573653L;

    /**
     * 首页广告位id
     */
    @ApiModelProperty(value = "首页广告位id")
    private String advertisingId;

    /**
     * 删除人
     */
    @ApiModelProperty(value = "删除人")
    private String delPerson;

}
