package com.wanmi.sbc.setting.api.response.systemstoreconfig;

import com.wanmi.sbc.setting.bean.vo.ConfigVO;
import com.wanmi.sbc.setting.bean.vo.SystemConfigVO;
import com.wanmi.sbc.setting.bean.vo.SystemStoreConfigVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/***
 * @desc  
 * @author shiy  2023/7/4 9:05
*/
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemStoreConfigResponse {

    @ApiModelProperty(value = "系统配置信息")
    SystemStoreConfigVO systemStoreConfigVO;
}
