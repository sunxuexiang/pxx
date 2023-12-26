package com.wanmi.sbc.returnorder.api.request.orderpicking;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/4/12 14:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class OrderPickingRequest implements Serializable {

    /**
     * 交易id
     */
    @ApiModelProperty(value = "交易id")
    private String tid;

    /**
     * 交易id
     */
    @ApiModelProperty(value = "交易集合")
    private List<String> tidList;

}
