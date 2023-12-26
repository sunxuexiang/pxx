package com.wanmi.sbc.goods.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author shiGuangYi
 * @createDate 2023-07-20 16:57
 * @Description: 首页全局检索
 * @Version 1.0
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IndexAllSearchRequest  implements Serializable {
    private static final long serialVersionUID = -1102900234709445736L;
    @ApiModelProperty(value = "市场Id")
    @NotNull
    private Long marketId;
    @ApiModelProperty(value = "搜索关键字")
    private String keyword;

    @ApiModelProperty(value = "用户Id")
    private String customerId;

}
