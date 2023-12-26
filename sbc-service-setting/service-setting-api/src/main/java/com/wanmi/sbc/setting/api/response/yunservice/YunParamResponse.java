package com.wanmi.sbc.setting.api.response.yunservice;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>根据id查询云配置信息response</p>
 * @author yang
 * @date 2019-11-05 18:33:04
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YunParamResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 键
     */
    @ApiModelProperty(value = "键")
    private String accessid;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private String host;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String policy;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String signature;

    /**
     * 状态,0:未启用1:已启用
     */
    @ApiModelProperty(value = "状态,0:未启用1:已启用")
    private Long expire;


    /**
     * 请求路径
     */
    @ApiModelProperty(value = "请求路径")
    private String dir;

}
