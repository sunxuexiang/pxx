package com.wanmi.sbc.goods.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
public class GoodsMallPlatformMarketRequest implements Serializable {
    private static final long serialVersionUID = -931818652800826454L;

    private Long marketId;

    private String marketName;

    private Long cityId;

    private Long provinceId;
}
