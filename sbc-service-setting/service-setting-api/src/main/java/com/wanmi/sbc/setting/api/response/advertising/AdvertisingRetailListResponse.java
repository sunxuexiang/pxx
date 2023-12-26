package com.wanmi.sbc.setting.api.response.advertising;

import com.wanmi.sbc.setting.bean.vo.AdvertisingRetailVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 获取有效散批广告位列表数据
 * @author: XinJiang
 * @time: 2022/4/19 11:26
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdvertisingRetailListResponse implements Serializable {

    private static final long serialVersionUID = 9020468212298509751L;

    /**
     * 首页广告位列表信息
     */
    @ApiModelProperty(value = "首页广告位列表信息")
    private List<AdvertisingRetailVO> advertisingRetailVOList;
}
