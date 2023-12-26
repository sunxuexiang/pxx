package com.wanmi.sbc.shopcart.bean.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class NumStepDTO implements Serializable {

    private static final long serialVersionUID = 2973899410241708605L;

    private Long num;

    private BigDecimal addStep;
}
