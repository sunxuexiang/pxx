package com.wanmi.sbc.account.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
public class WalletConfigDTO implements Serializable {
    private static final long serialVersionUID = 875263206212607535L;

    private Long id;

    @ApiModelProperty(value = "钱包限制的品牌id集合")
    private List<Long> goodsBrandIds;

    @ApiModelProperty(value = "钱包限制的分类id集合")
    private List<Long> goodsCateIds;

    @ApiModelProperty(value = "余额限制的商品id集合")
    private List<String> goodsInfoIds;
}
