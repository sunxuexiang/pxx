package com.wanmi.sbc.common.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author shiGuangYi
 * @createDate 2023-07-18 9:58
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImPortraitSetVO implements Serializable {
    @ApiModelProperty(value = "设置账号")
    private String From_Account;
    @ApiModelProperty(value = "明细")
    private List<ProfileItemDO> ProfileItem;
}
