package com.wanmi.sbc.wallet.wallet.model.root;

import com.wanmi.sbc.common.enums.DefaultFlag;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "wallet_config")
public class WalletConfig implements Serializable {
    private static final long serialVersionUID = 8386444122996321879L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 余额限制消费的品牌id集合，id之间用,隔开
     */
    @Column(name = "goods_brand_ids")
    private String goodsBrandIds;

    /**
     * 余额限制消费的分类id集合,id之间用,隔开
     */
    @Column(name = "goods_cate_ids")
    private String goodsCateIds;

    /**
     * 余额限制消费的商品id集合,id之间用,隔开
     */
    @Column(name = "goods_info_ids")
    private String goodsInfoIds;

    /**
     * 删除标识
     */
    @Column(name = "del_flag")
    @Enumerated
    private DefaultFlag delFlag;
}
