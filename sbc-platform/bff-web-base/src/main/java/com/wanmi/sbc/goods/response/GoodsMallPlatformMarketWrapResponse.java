package com.wanmi.sbc.goods.response;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description: 批发市场列表
 * @author: gdq
 * @create: 2023-06-16 08:57
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "批发市场列表")
public class GoodsMallPlatformMarketWrapResponse implements Serializable {

    private static final long serialVersionUID = 4239429839327965050L;

    private Long provinceId;

    private String provinceName;

    private BigDecimal sort;

    private List<GoodsMallPlatformMarketResponse> markets;

    private String provinceShortName;
}
