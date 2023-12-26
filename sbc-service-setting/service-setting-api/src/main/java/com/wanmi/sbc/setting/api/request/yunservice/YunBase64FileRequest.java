package com.wanmi.sbc.setting.api.request.yunservice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>云上传文件参数</p>
 *
 * @author yang
 * @date 2023-07-25 18:33:04
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YunBase64FileRequest implements Serializable {

    /**
     * Base64编码文件内容
     */
    @ApiModelProperty(value = "Base64编码文件内容")
    private String content;

}
