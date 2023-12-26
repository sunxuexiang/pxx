package com.wanmi.sbc.returnorder.api.request.trade;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class PickGoodsRequest implements Serializable {

   /* *//**
     * 查询几天内的数据
     *//*
    private Integer days = 0;*/

    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 开始时间
     */
    private String endTime;

   /* *//**
     * 月度（自然月）
     * 示例 2021-08-01
     *//*
    private String month;*/

    /**
     * 显示数据量
     */
    private Integer size = 30;

    /**
     * 排序方式（asc、desc）
     */
    private String sort = "asc";

    /**
     * token
     */
    private String token;
}
