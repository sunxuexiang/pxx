package com.wanmi.sbc.wms.api.response.record;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.wms.bean.vo.RecordVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>请求记录分页结果</p>
 * @author baijz
 * @date 2020-05-06 19:23:45
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 请求记录分页结果
     */
    @ApiModelProperty(value = "请求记录分页结果")
    private MicroServicePage<RecordVO> recordVOPage;
}
