package com.wanmi.sbc.mall.homepage.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-12-13 08:50
 **/
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class HomePageCatRequest extends HomePageBaseQueryDTO implements Serializable {
    private static final long serialVersionUID = 5470901803563643293L;

    @ApiModelProperty(value = "商城id")
    @NotNull
    private Long mallId;

    @ApiModelProperty(value = "类目id")
    @NotNull
    private Long catId;
}
