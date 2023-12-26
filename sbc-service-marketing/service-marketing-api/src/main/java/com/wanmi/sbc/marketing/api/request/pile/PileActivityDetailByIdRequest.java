package com.wanmi.sbc.marketing.api.request.pile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 根据id查询囤货活动视图详情的请求结构
 *
 * @author chenchang
 * @since 2022/09/06
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PileActivityDetailByIdRequest implements Serializable {

    private static final long serialVersionUID = -1452320752582496479L;

    /**
     * 囤货活动id
     */
    @ApiModelProperty(value = "囤货活动Id")
    @NotBlank
    private String activityId;
}
