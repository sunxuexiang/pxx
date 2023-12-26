package com.wanmi.sbc.returnorder.api.response.pickuprecord;

import com.wanmi.sbc.returnorder.bean.vo.PickUpRecordVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>测试代码生成修改结果</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickUpRecordModifyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的测试代码生成信息
     */
    @ApiModelProperty(value = "已修改的测试代码生成信息")
    private PickUpRecordVO pickUpRecordVO;
}
