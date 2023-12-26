package com.wanmi.sbc.setting.api.request.imonlineservice;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>IM客服常用语</p>
 * @date 2023-10-13 16:10:28
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceChatSearchRequest extends BaseQueryRequest {

    @ApiModelProperty(value = "聊天群组ID")
    private List<String> groupIdList;

    /**
     * 发送方账号
     */
    @ApiModelProperty(value = "发送方账号")
    private String fromAccount;
}
