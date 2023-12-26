package com.wanmi.sbc.order.api.response.pickuprecord;

import com.wanmi.sbc.order.bean.vo.PickUpRecordVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>测试代码生成新增结果</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickUpRecordAddResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已新增的测试代码生成信息
     */
    @ApiModelProperty(value = "已新增的测试代码生成信息")
    private PickUpRecordVO pickUpRecordVO;
}
