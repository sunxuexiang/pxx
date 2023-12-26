package com.wanmi.sbc.setting.api.response.advertising;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.bean.vo.StartPageAdvertisingVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 启动页广告配置信息分页响应实体类
 * @author: XinJiang
 * @time: 2022/3/31 14:27
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartPageAdvertisingPageResponse {

    /**
     * 分页数据
     */
    @ApiModelProperty(value = "首页广告位分页信息")
    private MicroServicePage<StartPageAdvertisingVO> advertisingPage;
}
