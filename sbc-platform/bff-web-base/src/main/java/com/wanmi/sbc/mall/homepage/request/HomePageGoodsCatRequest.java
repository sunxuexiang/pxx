package com.wanmi.sbc.mall.homepage.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-12-13 08:50
 **/
@Data
public class HomePageGoodsCatRequest extends HomePageBaseQueryDTO implements Serializable {
    private static final long serialVersionUID = 5470901803563643293L;

//    @NonNull
    private Long mallId;

//    @NonNull
    private Long catId;

}
