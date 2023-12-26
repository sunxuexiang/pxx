package com.wanmi.sbc.goods.api.response.goods;

import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import lombok.Data;

import java.util.List;

@Data
public class GoodsInsidePageResponse {
    private List<GoodsVO> content;
    private Integer total ;
    private Integer totalElements;
    private Integer totalPages;
    private Integer size;
}
