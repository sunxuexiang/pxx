package com.wanmi.sbc.setting.api.response.advertising;

import com.wanmi.sbc.setting.bean.vo.StartPageAdvertisingVO;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

/**
 * @description: 启动页广告配置信息响应实体类
 * @author: XinJiang
 * @time: 2022/3/31 14:26
 */
@ApiModel
@Data
@Builder
public class StartPageAdvertisingResponse extends StartPageAdvertisingVO {

    private static final long serialVersionUID = 2265298400917312285L;

}
