package com.wanmi.sbc.setting.api.request.onlineserviceitem;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>在线客服快捷回复常用语分组</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceCommonMessageGroupRequest implements Serializable {

    @ApiModelProperty(value = "分组ID")
    private Long groupId;

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
     * 分组名称
     */
    @ApiModelProperty(value = "分组名称")
    private String groupName;

    /**
     * 分组层级
     */
    @ApiModelProperty(value = "分组层级")
    private Integer groupLevel;


    /**
     * 上级分组ID，0表示一级分组
     */
    @ApiModelProperty(value = "上级分组ID，0表示一级分组")
    private Long parentGroupId;
}
