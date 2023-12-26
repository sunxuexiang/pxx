package com.wanmi.sbc.mall.homepage.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-12-20 15:06
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomePageBaseQueryDTO implements Serializable {
    private static final long serialVersionUID = -5867304080355665961L;

    @NonNull
    private Integer pageNum;

    @NonNull
    private Integer pageSize;

    @NonNull
    private Long marketId;
}
