package com.wanmi.sbc.setting.api.response.villagesaddress;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.bean.vo.VillagesAddressConfigVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description: 乡镇件地址配置信息分页响应实体类
 * @author: XinJiang
 * @time: 2022/4/29 11:19
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillagesAddressConfigPageResponse implements Serializable {

    private static final long serialVersionUID = 2022913683367933437L;

    /**
     * 分页数据
     */
    @ApiModelProperty("乡镇件地址配置分页数据")
    private MicroServicePage<VillagesAddressConfigVO> pageVillages;
}
