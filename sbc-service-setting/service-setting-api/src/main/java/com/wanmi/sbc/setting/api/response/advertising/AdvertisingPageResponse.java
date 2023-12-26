package com.wanmi.sbc.setting.api.response.advertising;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.bean.vo.AdvertisingVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description: 分页查询首页广告位信息响应实体类
 * @author: XinJiang
 * @time: 2022/2/18 15:07
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisingPageResponse implements Serializable {

    private static final long serialVersionUID = -8776585310226985821L;

    /**
     * 分页数据
     */
    @ApiModelProperty(value = "首页广告位分页信息")
    private MicroServicePage<AdvertisingVO> advertisingPage;

}
