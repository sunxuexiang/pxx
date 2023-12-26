package com.wanmi.sbc.mall.homepage.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-12-13 08:50
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomePageGoodsRequest extends HomePageBaseQueryDTO implements Serializable {
    private static final long serialVersionUID = 5470901803563643293L;

    private Long mallId;
}
