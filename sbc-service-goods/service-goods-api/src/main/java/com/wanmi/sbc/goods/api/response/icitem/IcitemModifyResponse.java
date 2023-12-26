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
 * <p>配送到家修改结果</p>
 * @author lh
 * @date 2020-12-05 18:16:34
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IcitemModifyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的配送到家信息
     */
    @ApiModelProperty(value = "已修改的配送到家信息")
    private IcitemVO icitemVO;
}
