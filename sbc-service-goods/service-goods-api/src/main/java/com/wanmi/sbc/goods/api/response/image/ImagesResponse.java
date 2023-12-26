package com.wanmi.sbc.goods.api.response.image;

import com.wanmi.sbc.goods.bean.vo.GoodsImageVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagesResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    List<GoodsImageVO> imageVOS;
}
