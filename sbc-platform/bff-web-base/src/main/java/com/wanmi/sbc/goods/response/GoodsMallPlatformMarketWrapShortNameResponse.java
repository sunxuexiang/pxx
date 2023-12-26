package com.wanmi.sbc.goods.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
public class GoodsMallPlatformMarketWrapShortNameResponse implements Serializable {

    private static final long serialVersionUID = 4239429839327965050L;
    private String id;

    private String name;
}
