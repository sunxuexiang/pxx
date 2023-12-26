package com.wanmi.sbc.goods.api.request.goods;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GoodsTagRelReRequest implements Serializable {

    private static final long serialVersionUID = 4175108058792167026L;
    private Long tagId;

    private String goodsId;

    private List<Long> tagIds;

    private List<String> goodsIds;
}
