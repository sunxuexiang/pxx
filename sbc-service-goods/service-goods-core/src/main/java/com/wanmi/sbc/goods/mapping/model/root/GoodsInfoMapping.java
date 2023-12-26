package com.wanmi.sbc.goods.mapping.model.root;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "goods_info_mapping")
public class GoodsInfoMapping implements Serializable {

    /**
     * 商品编号，采用UUID
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "parent_goods_info_id")
    private String parentGoodsInfoId;

    @Column(name = "goods_info_id")
    private String goodsInfoId;

    @Column(name = "erp_goods_info_no")
    private String erpGoodsInfoNo;

    @Column(name = "ware_id")
    private Long wareId;

}
