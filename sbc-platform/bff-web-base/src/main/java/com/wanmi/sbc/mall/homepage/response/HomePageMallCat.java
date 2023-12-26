package com.wanmi.sbc.mall.homepage.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HomePageMallCat implements Serializable {
    private static final long serialVersionUID = -5231361615074468979L;

    private Long mallId;

    private String mallName;

    private List<HomePageCatSimpleRes> goodsCats;
}
