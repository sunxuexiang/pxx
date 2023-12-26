package com.wanmi.sbc.wms.api.response.record;

import com.wanmi.sbc.wms.bean.vo.RecordVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>请求记录修改结果</p>
 * @author baijz
 * @date 2020-05-06 19:23:45
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordModifyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的请求记录信息
     */
    @ApiModelProperty(value = "已修改的请求记录信息")
    private RecordVO recordVO;
}
