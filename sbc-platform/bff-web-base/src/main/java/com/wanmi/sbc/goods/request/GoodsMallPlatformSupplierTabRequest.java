package com.wanmi.sbc.goods.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 * @program: sbc-backgroud
 * @description: 市场列表
 * @author: gdq
 * @create: 2023-06-16 09:02
 **/
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GoodsMallPlatformSupplierTabRequest implements Serializable {
    private static final long serialVersionUID = -931818652800826454L;

    @ApiModelProperty(value = "市场Id")
    @NotNull
    private Long marketId;

    @ApiModelProperty(value = "广告位Id")
    private String advertisingId;

    @ApiModelProperty(value = "广告配置Id")
    private String advertisingConfigId;

    @ApiModelProperty(value = "搜索关键字")
    private String keyword;

    @ApiModelProperty(value = "搜索关键字商城分类")
    private Set<Long> keywordFilterTagIds;
}
