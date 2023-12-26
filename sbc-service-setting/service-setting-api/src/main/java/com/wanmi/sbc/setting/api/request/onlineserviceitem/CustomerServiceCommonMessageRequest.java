package com.wanmi.sbc.setting.api.request.onlineserviceitem;


import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>客服快捷回复常用语表</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceCommonMessageRequest extends BaseQueryRequest {

    @ApiModelProperty(value = "消息ID")
    private Long msgId;

    /**
     * 公司ID
     */
    @ApiModelProperty(value = "公司ID")
    private Long companyInfoId;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String message;

    /**
     * 排序，升序
     */
    @ApiModelProperty(value = "排序，升序")
    private Integer sortNum;

    /**
     * 一级分组ID
     */
    @ApiModelProperty(value = "一级分组ID")
    private Long oneGroupId;

    /**
     * 二级分组ID
     */
    @ApiModelProperty(value = "二级分组ID")
    private Long secondGroupId;
}
