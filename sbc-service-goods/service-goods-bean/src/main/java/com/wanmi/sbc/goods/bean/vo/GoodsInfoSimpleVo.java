package com.wanmi.sbc.goods.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/5/29 14:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsInfoSimpleVo {

    private String goodsInfoId;

    private String goodsInfoName;

    private String goodsId;

    private String goodsImage;

    private String goodsSubtitle;


}
