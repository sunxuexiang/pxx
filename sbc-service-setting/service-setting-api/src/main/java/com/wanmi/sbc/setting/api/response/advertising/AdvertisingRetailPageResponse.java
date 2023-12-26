package com.wanmi.sbc.setting.api.response.advertising;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.bean.vo.AdvertisingRetailVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description: 散批广告位分页响应类
 * @author: XinJiang
 * @time: 2022/4/19 11:22
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisingRetailPageResponse implements Serializable {

    private static final long serialVersionUID = -7982417014264747977L;

    /**
     * 分页数据
     */
    @ApiModelProperty(value = "首页广告位分页信息")
    private MicroServicePage<AdvertisingRetailVO> advertisingRetailPage;
}
