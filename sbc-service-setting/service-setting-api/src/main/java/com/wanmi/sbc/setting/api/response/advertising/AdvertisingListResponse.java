package com.wanmi.sbc.setting.api.response.advertising;

import com.wanmi.sbc.setting.bean.vo.AdvertisingVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 获取有效首页广告位列表
 * @author: XinJiang
 * @time: 2022/2/18 17:06
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdvertisingListResponse implements Serializable {

    private static final long serialVersionUID = 811287838187148583L;

    /**
     * 首页广告位列表信息
     */
    @ApiModelProperty(value = "首页广告位列表信息")
    private List<AdvertisingVO> advertisingVOList;
}
