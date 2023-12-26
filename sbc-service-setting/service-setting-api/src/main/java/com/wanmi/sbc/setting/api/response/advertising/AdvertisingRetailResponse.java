package com.wanmi.sbc.setting.api.response.advertising;

import com.wanmi.sbc.setting.bean.vo.AdvertisingRetailVO;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

/**
 * @description: 散批广告位请求响应类
 * @author: XinJiang
 * @time: 2022/4/19 11:18
 */
@ApiModel
@Data
@Builder
public class AdvertisingRetailResponse extends AdvertisingRetailVO {

    private static final long serialVersionUID = -8365696697297171095L;

}
