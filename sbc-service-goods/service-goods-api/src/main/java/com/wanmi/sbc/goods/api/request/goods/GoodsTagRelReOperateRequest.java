package com.wanmi.sbc.goods.api.request.goods;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GoodsTagRelReOperateRequest implements Serializable {

    private static final long serialVersionUID = 4175108058792167026L;
    private Long tagId;

    // 1 add , 2: remove
    private Integer type;
    
    private List<String> goodsIds;
}
