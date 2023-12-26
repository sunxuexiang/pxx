package com.wanmi.sbc.job.model.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>im 历史数据解析</p>
 * @Author shiGuangYi
 * @createDate 2023-06-29 16:03
 * @Description: TODO
 * @Version 1.0
 */

@Data
@ApiModel
public class ImHistoryVO implements Serializable {
    private List<ImFileVO> File;
    private String ActionStatus;
    private String ErrorInfo;
    private int ErrorCode;
}
