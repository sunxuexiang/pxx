package com.wanmi.sbc.order.api.response.pickuprecord;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.order.bean.vo.PickUpRecordVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>测试代码生成分页结果</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickUpRecordPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 测试代码生成分页结果
     */
    @ApiModelProperty(value = "测试代码生成分页结果")
    private MicroServicePage<PickUpRecordVO> pickUpRecordVOPage;
}
