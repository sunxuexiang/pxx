package com.wanmi.sbc.goods.api.response.lastgoodswrite;

import com.wanmi.sbc.goods.bean.vo.LastGoodsWriteVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>用户最后一次商品记录列表结果</p>
 * @author 费传奇
 * @date 2021-04-23 17:33:51
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LastGoodsWriteListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户最后一次商品记录列表结果
     */
    @ApiModelProperty(value = "用户最后一次商品记录列表结果")
    private List<LastGoodsWriteVO> lastGoodsWriteVOList;
}
