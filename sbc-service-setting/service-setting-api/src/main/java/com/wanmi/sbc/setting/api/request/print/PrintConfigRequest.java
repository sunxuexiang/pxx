package com.wanmi.sbc.setting.api.request.print;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author dyt
 * @date 2020-03-30 14:39:57
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrintConfigRequest implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @NotNull
    private Long printId;


    /**
     * 打印头部
     */
    private String printHead;


    /**
     * 打印尾部
     */
    private String printBottom;

}