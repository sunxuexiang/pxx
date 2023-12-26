package com.wanmi.ares.request.screen;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lm
 * @date 2022/09/08 17:08
 */
@Data
public class RealTimeOrderRequest implements Serializable {

    // 当前页面
    private Integer current;

    // 页面大小
    private Integer pageSize;

    // 日期
    private String day;
}
