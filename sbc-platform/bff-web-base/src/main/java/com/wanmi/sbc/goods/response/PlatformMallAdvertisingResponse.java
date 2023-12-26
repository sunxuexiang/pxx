package com.wanmi.sbc.goods.response;

import com.wanmi.sbc.setting.bean.vo.AdvertisingVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-25 17:12
 **/
@Data
public class PlatformMallAdvertisingResponse implements Serializable {


    /**
     * 首页广告位列表信息
     */
    @ApiModelProperty(value = "首页广告位列表信息")
    private List<AdvertisingVO> advertisingVOList;
}
