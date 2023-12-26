package com.wanmi.sbc.setting.api.request.systemfile;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量移动平台文件请求
 * @author hudong
 * @date 2023-09-08 10:01:22
 */
@Getter
@Setter
public class SystemFileMoveRequest {

    /**
     * 批量ID
     */
    @NotEmpty
    private List<Long> ids;
    /**
     * 文件路径
     */
    private String path;

}
