package com.wanmi.sbc.setting.api.response.advertising;

import com.wanmi.sbc.setting.bean.vo.AdvertisingVO;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

/**
 * @description: 首页广告位响应实体类
 * @author: XinJiang
 * @time: 2022/2/18 15:04
 */
@ApiModel
@Data
@Builder
public class AdvertisingResponse extends AdvertisingVO {

    private static final long serialVersionUID = 7011547401922506029L;

}
