package com.wanmi.sbc.setting.api.response.villagesaddress;

import com.wanmi.sbc.setting.bean.vo.VillagesAddressConfigVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 乡镇件地址配置信息列表响应类
 * @author: XinJiang
 * @time: 2022/4/29 11:00
 */
@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class VillagesAddressConfigListResponse implements Serializable {

    private static final long serialVersionUID = 3126707491077720137L;

    /**
     * 乡镇件地址配置信息列表
     */
    @ApiModelProperty(value = "乡镇件地址配置信息列表")
    private List<VillagesAddressConfigVO> villagesAddressConfigVOList;
}
