package com.wanmi.sbc.setting.api.response.print;

import com.wanmi.sbc.setting.bean.vo.PrintConfigVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class PrintConfigResponse implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * 打印实体
     */
    private PrintConfigVO printConfigVO;

}