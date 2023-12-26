package com.wanmi.sbc.returnorder.api.response.pickuprecord;

import com.wanmi.sbc.returnorder.bean.vo.PickUpRecordVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>测试代码生成列表结果</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickUpRecordListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 测试代码生成列表结果
     */
    @ApiModelProperty(value = "测试代码生成列表结果")
    private List<PickUpRecordVO> pickUpRecordVOList;
}
