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
 * <p>根据id查询任意（包含已删除）onlineService信息response</p>
 * * @Author shiGuangYi
 * @createDate 2023-06-05 17:30
 * @Description: 腾讯客服  IM
 * @Version 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImOnlineServiceByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ImOnlineService信息
     */
    @ApiModelProperty(value = "imOnlineService信息")
    private ImOnlineServiceVO onlineServiceVO;
}
