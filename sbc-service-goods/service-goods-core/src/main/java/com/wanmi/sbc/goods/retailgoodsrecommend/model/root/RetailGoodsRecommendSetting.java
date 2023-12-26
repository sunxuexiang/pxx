package com.wanmi.sbc.goods.retailgoodsrecommend.model.root;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @description: 散批鲸喜推荐商品配置信息实体类
 * @author: XinJiang
 * @time: 2022/4/20 9:11
 */
@Data
@Entity
@Table(name = "retail_goods_recommend")
public class RetailGoodsRecommendSetting implements Serializable {

    private static final long serialVersionUID = -3702554224733666287L;

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "recommend_id")
    private String recommendId;

    /**
     * 商品skuId
     */
    @Column(name = "goods_info_id")
    private String goodsInfoId;

    /**
     * 排序顺序
     */
    @Column(name = "sort_num")
    private Integer sortNum;
}
