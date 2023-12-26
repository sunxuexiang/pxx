package com.wanmi.sbc.setting.api.request.onlineservice;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceSettingContentVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImChatRequest extends BaseQueryRequest {

    @ApiModelProperty(value = "群组ID")
    private String imGroupId;

    @ApiModelProperty(value = "公司ID")
    private Long companyId;

    @ApiModelProperty(value = "店铺ID")
    private Long storeId;

    private String appKey;

    private Long appId;

    private Integer settingType;

    private CustomerServiceSettingContentVo settingContent;

    private String storeName;

    private Integer limit;

    @ApiModelProperty(value = "客服账号")
    private String serverAccount;

    private String message;

    private String groupImg;

    private String messageType;

}
