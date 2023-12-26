package com.wanmi.sbc.setting.api.request.villagesaddress;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 通过id删除乡镇件地址配置信息请求参数类
 * @author: XinJiang
 * @time: 2022/4/29 10:58
 */
@Data
@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class VillagesAddressConfigDelByIdsRequest implements Serializable {

    private static final long serialVersionUID = -6372365187085854595L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id集合")
    private List<Long> ids;
}
