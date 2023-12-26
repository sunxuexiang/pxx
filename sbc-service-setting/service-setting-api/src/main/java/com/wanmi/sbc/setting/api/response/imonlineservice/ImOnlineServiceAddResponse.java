package com.wanmi.sbc.setting.api.response.imonlineservice;

import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceVO;
import com.wanmi.sbc.setting.bean.vo.OnlineServiceVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * <p>onlineService新增结果</p>
 * @Author shiGuangYi
 * @createDate 2023-06-05 17:30
 * @Description: 腾讯客服  IM
 * @Version 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImOnlineServiceAddResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已新增的ImOnlineService信息
     */
    @ApiModelProperty(value = "已新增的imOnlineService信息")
    private ImOnlineServiceVO onlineServiceVO;
}
