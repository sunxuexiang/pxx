package com.wanmi.sbc.marketing.bean.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/5/28 15:17
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoinGoodsVo implements Serializable {

    private static final long serialVersionUID = 7818187012635710248L;

    private String goodsInfoId;

    private String coinActivityId;

    private String goodsName;

    private String pic;

    private String unit;

    private Long num;

    private BigDecimal totalCoinNum;
}
