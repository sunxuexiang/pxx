package com.wanmi.sbc.goods;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-08-16 08:58
 **/
@Data
public class GoodsMallPlatformGifUrlHomePageVO implements Serializable {
    private static final long serialVersionUID = 8592786624961410530L;

    private Integer id;

    private String url;

    private String urlImage;
}
