package com.wanmi.sbc.goods.api.response.icitem;

import com.wanmi.sbc.goods.bean.vo.IcitemVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>根据id查询任意（包含已删除）配送到家信息response</p>
 * @author lh
 * @date 2020-12-05 18:16:34
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IcitemByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 配送到家信息
     */
    @ApiModelProperty(value = "配送到家信息")
    private IcitemVO icitemVO;
}
