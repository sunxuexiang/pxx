package com.wanmi.sbc.setting.api.request.imonlineservice;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

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
public class ImOnlineServiceDelMsgRequest extends SettingBaseRequest {

    /**
     * 客服账号
     */
    @ApiModelProperty(value = "客服账号")
    private String customerServiceAccount;
    @ApiModelProperty(value = "会话类型：1 表示 C2C 会话；2 表示 G2C 会话")
    private String type;


}
