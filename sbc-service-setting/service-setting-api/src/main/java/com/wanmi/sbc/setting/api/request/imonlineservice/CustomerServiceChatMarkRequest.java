package com.wanmi.sbc.setting.api.request.imonlineservice;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceChatMarkRequest {

    /**
     * 客服服务账号
     */
    private String serverAccount;

    /**
     * 公司ID
     */
    private Long companyInfoId;

    /**
     * 腾讯IM群组ID
     */
    private String imGroupId;

    /**
     * 标记日期
     */
    private String markDate;
}
