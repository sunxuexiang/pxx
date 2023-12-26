package com.wanmi.sbc.goods.api.request.brand;

import com.wanmi.sbc.goods.bean.dto.GoodsBrandDTO;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

/**
 * 品牌排序批量修改
 * Created by yang on 2020/12/31.
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsBrandBatchSortModifyRequest {

    private static final long serialVersionUID = 1L;

    List<GoodsBrandDTO> goodsBrandDTOList;
}
