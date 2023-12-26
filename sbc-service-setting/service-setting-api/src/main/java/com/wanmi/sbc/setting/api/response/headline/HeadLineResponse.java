package com.wanmi.sbc.setting.api.response.headline;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/7 15:12
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeadLineResponse implements Serializable {

    private static final long serialVersionUID = 6806248353320291807L;

    @ApiModelProperty(value = "ID")
    private String headlineId;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "排序")
    private Integer sortNum;

    @ApiModelProperty(value = "速度")
    private BigDecimal speeds;

    @ApiModelProperty(value = "总秒数")
    private BigDecimal totalSeconds;


}
