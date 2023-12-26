package com.wanmi.sbc.setting.api.response.systemfile;

import com.wanmi.sbc.setting.bean.vo.SystemFileVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 平台文件信息列表结果
 * @author hudong
 * @date 2023-09-08 16:12:49
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemFileListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 平台文件信息列表结果
     */
    @ApiModelProperty(value = "平台文件信息列表结果")
    private List<SystemFileVO> systemFileVOList;
}
