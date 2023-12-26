package com.wanmi.sbc.mall.homepage.response;


import lombok.Data;

import java.io.Serializable;

@Data
public class HomePageCatSimpleRes implements Serializable {
    private static final long serialVersionUID = 1871518004221478159L;
    private Long cateId;

    private String cateName;

    private String cateImg;
}
