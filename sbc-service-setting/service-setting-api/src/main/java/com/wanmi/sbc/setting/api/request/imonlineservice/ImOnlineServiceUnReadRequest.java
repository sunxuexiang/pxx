package com.wanmi.sbc.setting.api.request.imonlineservice;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-06 14:04
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImOnlineServiceUnReadRequest extends SettingBaseRequest {

    /**
     * 客服账号
     */
    @ApiModelProperty(value = "客服账号")
    private String customerServiceAccount;
    @ApiModelProperty(value = "会话放集合")
    private List<String> userId;


}
