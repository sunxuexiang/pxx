package com.wanmi.sbc.mall.homepage.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class HomePageCatResponse implements Serializable {
    private static final long serialVersionUID = 5470901803563643293L;

    // 分类数组
    private List<HomePageMallCat> cats;
}
