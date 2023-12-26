package com.wanmi.sbc.job.model.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-29 16:03
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ApiModel
public class ImFileVO implements Serializable {
    private String URL;
    private Date ExpireTime;
    private int FileSize;
    private String FileMD5;
    private int GzipSize;
    private String GzipMD5;
}
