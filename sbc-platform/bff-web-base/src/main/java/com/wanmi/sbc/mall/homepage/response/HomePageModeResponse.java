package com.wanmi.sbc.mall.homepage.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-12-20 14:58
 **/
@Data
public class HomePageModeResponse implements Serializable {
    private static final long serialVersionUID = -488767445819876857L;
    // 1: 商家模式，2：产品模式，3：商家产品模式，4：产品商家模式
    private Integer mode;
}
